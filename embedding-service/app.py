"""
LinkAI Embedding 服务
提供向量化和语义检索接口
"""
from flask import Flask, request, jsonify
from chroma_service import (
    add_document, add_documents, search,
    delete_document, get_all_documents,
    count_documents, reset_collection
)
import os

app = Flask(__name__)

# MiniMax API 配置
MINIMAX_API_KEY = os.getenv("MINIMAX_API_KEY", "")
MINIMAX_BASE_URL = "https://api.minimax.chat/v"


@app.route('/health', methods=['GET'])
def health_check():
    """健康检查"""
    return jsonify({
        "status": "ok",
        "service": "LinkAI Embedding Service",
        "documents_count": count_documents()
    })


@app.route('/embed', methods=['POST'])
def get_embedding():
    """
    获取文本向量（调用 MiniMax Embedding API）

    请求体: {"text": "要向量化的文本"}
    返回: {"embedding": [0.123, -0.456, ...]}
    """
    data = request.get_json()
    text = data.get('text', '')

    if not text:
        return jsonify({"error": "text is required"}), 400

    # 如果没有配置 API Key，返回 mock 数据（用于开发测试）
    if not MINIMAX_API_KEY:
        return jsonify({
            "embedding": [0.1] * 768,  # Mock 向量
            "warning": "Using mock embedding, set MINIMAX_API_KEY for real vectors"
        })

    try:
        import requests
        response = requests.post(
            f"{MINIMAX_BASE_URL}/text/embeddings",
            headers={
                "Authorization": f"Bearer {MINIMAX_API_KEY}",
                "Content-Type": "application/json"
            },
            json={
                "model": "embo-01",
                "texts": [text]
            },
            timeout=30
        )

        if response.status_code == 200:
            result = response.json()
            return jsonify({
                "embedding": result.get("data", [{}])[0].get("embedding", [])
            })
        else:
            return jsonify({"error": f"API error: {response.text}"}), 500

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/search', methods=['POST'])
def semantic_search():
    """
    语义检索（直接使用 Chroma，需要先注入向量）

    请求体: {
        "query": "查询文本",
        "n": 返回数量（默认5）
    }
    返回: {
        "documents": [...],
        "metadatas": [...],
        "distances": [...]
    }
    """
    data = request.get_json()
    query = data.get('query', '')
    n = data.get('n', 5)

    if not query:
        return jsonify({"error": "query is required"}), 400

    results = search(query, n)
    return jsonify(results)


@app.route('/add', methods=['POST'])
def add_knowledge():
    """
    添加知识库文档（需要先调用 /embed 获取向量）

    请求体: {
        "id": "文档ID",
        "content": "文档内容",
        "metadata": {"title": "标题", "source": "来源"}
    }
    """
    data = request.get_json()
    doc_id = data.get('id', '')
    content = data.get('content', '')
    metadata = data.get('metadata', {})

    if not doc_id or not content:
        return jsonify({"error": "id and content are required"}), 400

    success = add_document(doc_id, content, metadata)
    if success:
        return jsonify({"status": "ok", "id": doc_id})
    else:
        return jsonify({"error": "Failed to add document"}), 500


@app.route('/add-batch', methods=['POST'])
def add_knowledge_batch():
    """
    批量添加知识库文档

    请求体: {
        "documents": [
            {"id": "doc1", "content": "内容1", "metadata": {}},
            {"id": "doc2", "content": "内容2", "metadata": {}}
        ]
    }
    """
    data = request.get_json()
    documents = data.get('documents', [])

    if not documents:
        return jsonify({"error": "documents is required"}), 400

    doc_ids = [d['id'] for d in documents]
    contents = [d['content'] for d in documents]
    metadatas = [d.get('metadata', {}) for d in documents]

    success = add_documents(doc_ids, contents, metadatas)
    if success:
        return jsonify({"status": "ok", "count": len(documents)})
    else:
        return jsonify({"error": "Failed to add documents"}), 500


@app.route('/delete', methods=['POST'])
def delete_knowledge():
    """
    删除知识库文档

    请求体: {"id": "文档ID"}
    """
    data = request.get_json()
    doc_id = data.get('id', '')

    if not doc_id:
        return jsonify({"error": "id is required"}), 400

    success = delete_document(doc_id)
    if success:
        return jsonify({"status": "ok"})
    else:
        return jsonify({"error": "Failed to delete document"}), 500


@app.route('/list', methods=['GET'])
def list_knowledge():
    """获取所有知识库文档"""
    results = get_all_documents()
    return jsonify({
        "count": len(results.get("documents", [])),
        "documents": results
    })


@app.route('/count', methods=['GET'])
def knowledge_count():
    """获取知识库文档数量"""
    return jsonify({"count": count_documents()})


@app.route('/reset', methods=['POST'])
def reset_knowledge():
    """重置知识库（危险操作）"""
    success = reset_collection()
    if success:
        return jsonify({"status": "ok"})
    else:
        return jsonify({"error": "Failed to reset"}), 500


# ============== 便捷接口（整合 Embed + Add） ==============

@app.route('/upsert', methods=['POST'])
def upsert_with_embedding():
    """
    一键添加文档（自动调用 Embedding API）

    请求体: {
        "id": "文档ID",
        "content": "文档内容",
        "metadata": {"title": "标题"}
    }

    说明: 这个接口会先调用 MiniMax Embedding API 获取向量，
          然后存入 Chroma。如果 API Key 未配置，会返回错误。
    """
    data = request.get_json()
    doc_id = data.get('id', '')
    content = data.get('content', '')
    metadata = data.get('metadata', {})

    if not doc_id or not content:
        return jsonify({"error": "id and content are required"}), 400

    if not MINIMAX_API_KEY:
        return jsonify({"error": "MINIMAX_API_KEY not configured"}), 500

    try:
        import requests

        # 1. 获取向量
        embed_response = requests.post(
            f"{MINIMAX_BASE_URL}/text/embeddings",
            headers={
                "Authorization": f"Bearer {MINIMAX_API_KEY}",
                "Content-Type": "application/json"
            },
            json={
                "model": "embo-01",
                "texts": [content]
            },
            timeout=30
        )

        if embed_response.status_code != 200:
            return jsonify({"error": f"Embedding API error: {embed_response.text}"}), 500

        embedding = embed_response.json().get("data", [{}])[0].get("embedding", [])

        # 2. 存入 Chroma
        # 注意: Chroma 的 add 方法需要 embeddings 参数
        from chroma_service import collection
        collection.add(
            documents=[content],
            embeddings=[embedding],
            ids=[doc_id],
            metadatas=[metadata]
        )

        return jsonify({"status": "ok", "id": doc_id, "embedding_dim": len(embedding)})

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/upsert-batch', methods=['POST'])
def upsert_batch_with_embedding():
    """
    批量一键添加文档（自动调用 Embedding API）

    请求体: {
        "documents": [
            {"id": "doc1", "content": "内容1", "metadata": {}},
            {"id": "doc2", "content": "内容2", "metadata": {}}
        ]
    }
    """
    data = request.get_json()
    documents = data.get('documents', [])

    if not documents:
        return jsonify({"error": "documents is required"}), 400

    if not MINIMAX_API_KEY:
        return jsonify({"error": "MINIMAX_API_KEY not configured"}), 500

    try:
        import requests

        # 1. 批量获取向量
        contents = [d['content'] for d in documents]
        embed_response = requests.post(
            f"{MINIMAX_BASE_URL}/text/embeddings",
            headers={
                "Authorization": f"Bearer {MINIMAX_API_KEY}",
                "Content-Type": "application/json"
            },
            json={
                "model": "embo-01",
                "texts": contents
            },
            timeout=60
        )

        if embed_response.status_code != 200:
            return jsonify({"error": f"Embedding API error: {embed_response.text}"}), 500

        embeddings_data = embed_response.json().get("data", [])
        embeddings = [e.get("embedding", []) for e in embeddings_data]

        # 2. 批量存入 Chroma
        doc_ids = [d['id'] for d in documents]
        metadatas = [d.get('metadata', {}) for d in documents]

        from chroma_service import collection
        collection.add(
            documents=contents,
            embeddings=embeddings,
            ids=doc_ids,
            metadatas=metadatas
        )

        return jsonify({"status": "ok", "count": len(documents)})

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    print("=" * 50)
    print("LinkAI Embedding Service")
    print("=" * 50)
    print(f"API Key: {'✓ Configured' if MINIMAX_API_KEY else '✗ Not Set (using mock)'}")
    print(f"Port: 5000")
    print("=" * 50)
    app.run(host='0.0.0.0', port=5000, debug=True)
