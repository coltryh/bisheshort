"""
初始化 LinkAI 产品知识库
运行: python init_knowledge.py
"""
from chroma_service import add_documents, count_documents

# LinkAI 产品 FAQ 知识库内容
faq_data = [
    {
        "id": "faq_001",
        "content": "如何创建短链接？",
        "metadata": {"title": "创建短链接", "category": "基础操作", "source": "官方FAQ"}
    },
    {
        "id": "faq_002",
        "content": "在'我的空间'页面，点击'新建链接'按钮，输入您要缩短的原始链接（长链接），系统会自动生成一个短链接。您还可以自定义短链接的后缀、设置有效期和描述信息。",
        "metadata": {"title": "创建短链接", "category": "基础操作", "source": "官方FAQ"}
    },
    {
        "id": "faq_003",
        "content": "如何设置短链接的有效期？",
        "metadata": {"title": "设置有效期", "category": "高级功能", "source": "官方FAQ"}
    },
    {
        "id": "faq_004",
        "content": "创建短链接时，在有效期选项中选择：1. 永久有效 - 链接永不过期；2. 定时过期 - 设置具体的过期日期和时间；3. 点击次数限制 - 设置链接被访问的最大次数，达到后自动失效。设置后可以在管理后台随时修改。",
        "metadata": {"title": "设置有效期", "category": "高级功能", "source": "官方FAQ"}
    },
    {
        "id": "faq_005",
        "content": "支持批量创建短链接吗？",
        "metadata": {"title": "批量创建", "category": "高级功能", "source": "官方FAQ"}
    },
    {
        "id": "faq_006",
        "content": "支持批量创建！您可以：1. 使用CSV文件导入，格式为：原始链接,描述,有效期（每行一条）；2. 在批量创建页面粘贴多行URL列表；3. 使用API接口程序化创建。批量创建单次最多支持1000条。",
        "metadata": {"title": "批量创建", "category": "高级功能", "source": "官方FAQ"}
    },
    {
        "id": "faq_007",
        "content": "如何查看短链接的访问统计？",
        "metadata": {"title": "访问统计", "category": "数据分析", "source": "官方FAQ"}
    },
    {
        "id": "faq_008",
        "content": "在链接列表中点击对应链接的'详情'按钮，可以查看：1. 实时PV（页面浏览量）、UV（独立访客数）、IP数；2. 访问趋势图，按小时/日/周展示；3. 访问来源地区分布；4. 使用的设备和浏览器统计；5. 网络运营商分析。所有数据支持导出为Excel。",
        "metadata": {"title": "访问统计", "category": "数据分析", "source": "官方FAQ"}
    },
    {
        "id": "faq_009",
        "content": "链接打不开了怎么办？",
        "metadata": {"title": "故障排除", "category": "常见问题", "source": "官方FAQ"}
    },
    {
        "id": "faq_010",
        "content": "链接打不开的常见原因：1. 原链接失效 - 被访问的原始网站已下线或设置了访问限制；2. 链接已过期 - 检查是否设置了有效期；3. 链接已被删除 - 在回收站中查看是否被误删；4. 被安全软件拦截 - 部分企业网络会拦截短链接。请先确认原因，然后对应处理。",
        "metadata": {"title": "故障排除", "category": "常见问题", "source": "官方FAQ"}
    },
    {
        "id": "faq_011",
        "content": "如何自定义短链接的URL后缀？",
        "metadata": {"title": "自定义URL", "category": "基础操作", "source": "官方FAQ"}
    },
    {
        "id": "faq_012",
        "content": "创建链接时，在自定义后缀输入框中填写您想要的后缀（如：my-link），系统会生成 linkai.com/my-link 格式的短链接。注意：后缀只能包含字母、数字、中划线和下划线，长度3-32字符，且不能与已有链接重复。",
        "metadata": {"title": "自定义URL", "category": "基础操作", "source": "官方FAQ"}
    },
    {
        "id": "faq_013",
        "content": "AI智能分析功能有什么用？",
        "metadata": {"title": "AI分析", "category": "AI功能", "source": "官方FAQ"}
    },
    {
        "id": "faq_014",
        "content": "LinkAI集成了AI智能分析功能，可以：1. 分析访问趋势 - 识别流量高峰和异常波动；2. 用户画像分析 - 分析访问者的设备、地区、行为特征；3. 优化建议 - 基于数据给出提升链接效果的运营建议；4. 自动生成描述 - AI自动为您的链接生成吸引人的描述文案。",
        "metadata": {"title": "AI分析", "category": "AI功能", "source": "官方FAQ"}
    },
    {
        "id": "faq_015",
        "content": "如何联系客服？",
        "metadata": {"title": "联系客服", "category": "其他", "source": "官方FAQ"}
    },
    {
        "id": "faq_016",
        "content": "您可以通过以下方式联系我们的客服团队：1. 在线客服 - 点击页面右下角的'在线咨询'按钮；2. 邮件支持 - 发送邮件到 support@linkai.com；3. 智能客服 - 24小时AI客服随时为您解答常见问题；4. 工单系统 - 在'帮助中心'提交工单，我们会在24小时内回复。",
        "metadata": {"title": "联系客服", "category": "其他", "source": "官方FAQ"}
    }
]


def main():
    print("=" * 50)
    print("LinkAI 知识库初始化")
    print("=" * 50)

    # 检查是否已有数据
    existing_count = count_documents()
    print(f"当前知识库文档数: {existing_count}")

    if existing_count > 0:
        response = input("知识库已有数据，是否清空并重新初始化？(y/N): ")
        if response.lower() != 'y':
            print("取消初始化")
            return
        # 清空（通过不导入 reset 函数提示用户手动删除）
        print("请删除 chroma_data 文件夹后重新运行")

    # 初始化数据
    print(f"\n开始添加 {len(faq_data)} 条知识...")

    doc_ids = [d["id"] for d in faq_data]
    contents = [d["content"] for d in faq_data]
    metadatas = [d["metadata"] for d in faq_data]

    success = add_documents(doc_ids, contents, metadatas)

    if success:
        print(f"✓ 成功添加 {len(faq_data)} 条知识")
        print(f"✓ 当前知识库文档数: {count_documents()}")
    else:
        print("✗ 添加失败")


if __name__ == '__main__':
    main()
