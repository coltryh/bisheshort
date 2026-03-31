"""
Chroma 向量数据库服务封装
用于文档存储和语义检索
"""
import chromadb
from chromadb.config import Settings
import os

# Chroma 数据存储路径
CHROMA_DATA_PATH = os.path.join(os.path.dirname(__file__), "chroma_data")

# 初始化 Chroma 客户端（持久化存储）
chroma_client = chromadb.PersistentClient(path=CHROMA_DATA_PATH)

# 获取或创建知识库集合
collection = chroma_client.get_or_create_collection(
    name="knowledge_base",
    metadata={"description": "LinkAI 产品知识库"}
)


def add_document(doc_id: str, content: str, metadata: dict = None) -> bool:
    """
    添加文档到知识库

    Args:
        doc_id: 文档唯一ID
        content: 文档内容
        metadata: 元数据（来源、标题等）

    Returns:
        bool: 添加是否成功
    """
    try:
        collection.add(
            documents=[content],
            ids=[doc_id],
            metadatas=[metadata or {"source": "manual"}]
        )
        return True
    except Exception as e:
        print(f"添加文档失败: {e}")
        return False


def add_documents(doc_ids: list, contents: list, metadatas: list = None) -> bool:
    """
    批量添加文档

    Args:
        doc_ids: 文档ID列表
        contents: 文档内容列表
        metadatas: 元数据列表

    Returns:
        bool: 添加是否成功
    """
    try:
        collection.add(
            documents=contents,
            ids=doc_ids,
            metadatas=metadatas or [{"source": "manual"} for _ in contents]
        )
        return True
    except Exception as e:
        print(f"批量添加文档失败: {e}")
        return False


def search(query: str, n_results: int = 5) -> dict:
    """
    语义检索

    Args:
        query: 查询文本
        n_results: 返回结果数量

    Returns:
        dict: 包含 documents, metadatas, distances
    """
    try:
        results = collection.query(
            query_texts=[query],
            n_results=n_results
        )

        return {
            "documents": results["documents"][0] if results["documents"] else [],
            "metadatas": results["metadatas"][0] if results["metadatas"] else [],
            "distances": results["distances"][0] if results["distances"] else [],
            "ids": results["ids"][0] if results["ids"] else []
        }
    except Exception as e:
        print(f"检索失败: {e}")
        return {"documents": [], "metadatas": [], "distances": [], "ids": []}


def delete_document(doc_id: str) -> bool:
    """
    删除文档

    Args:
        doc_id: 文档ID

    Returns:
        bool: 删除是否成功
    """
    try:
        collection.delete(ids=[doc_id])
        return True
    except Exception as e:
        print(f"删除文档失败: {e}")
        return False


def get_all_documents() -> dict:
    """
    获取所有文档

    Returns:
        dict: 所有文档数据
    """
    try:
        results = collection.get()
        return {
            "documents": results.get("documents", []),
            "metadatas": results.get("metadatas", []),
            "ids": results.get("ids", [])
        }
    except Exception as e:
        print(f"获取文档失败: {e}")
        return {"documents": [], "metadatas": [], "ids": []}


def count_documents() -> int:
    """
    获取文档数量

    Returns:
        int: 文档数量
    """
    return collection.count()


def reset_collection() -> bool:
    """
    重置知识库（谨慎使用）

    Returns:
        bool: 重置是否成功
    """
    try:
        global collection
        chroma_client.delete_collection("knowledge_base")
        collection = chroma_client.get_or_create_collection(
            name="knowledge_base",
            metadata={"description": "LinkAI 产品知识库"}
        )
        return True
    except Exception as e:
        print(f"重置知识库失败: {e}")
        return False
