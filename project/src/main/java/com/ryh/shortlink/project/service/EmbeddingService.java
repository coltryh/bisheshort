/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ryh.shortlink.project.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ryh.shortlink.project.config.EmbeddingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Embedding 服务
 * 调用 Python Embedding 服务进行向量化和检索
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingConfig config;

    /**
     * 获取文本向量
     *
     * @param text 文本
     * @return 向量列表
     */
    public List<Double> getEmbedding(String text) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);

            HttpResponse response = HttpRequest.post(config.getBaseUrl() + "/embed")
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(requestBody))
                    .timeout(config.getReadTimeout())
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSON.parseObject(response.body());
                JSONArray embedding = result.getJSONArray("embedding");
                if (embedding != null) {
                    return embedding.toList(Double.class);
                }
            }
            log.warn("Embedding API 返回异常: {}", response.body());
        } catch (Exception e) {
            log.error("获取Embedding失败", e);
        }

        // 返回 mock 数据
        return generateMockEmbedding();
    }

    /**
     * 语义检索
     *
     * @param query 查询文本
     * @param topK  返回数量
     * @return 检索结果
     */
    public SearchResult search(String query, int topK) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", query);
            requestBody.put("n", topK > 0 ? topK : config.getSearchTopK());

            HttpResponse response = HttpRequest.post(config.getBaseUrl() + "/search")
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(requestBody))
                    .timeout(config.getReadTimeout())
                    .execute();

            if (response.isOk()) {
                JSONObject result = JSON.parseObject(response.body());
                JSONArray documents = result.getJSONArray("documents");
                JSONArray metadatas = result.getJSONArray("metadatas");
                JSONArray distances = result.getJSONArray("distances");

                SearchResult searchResult = new SearchResult();
                if (documents != null) {
                    searchResult.setDocuments(documents.toList(String.class));
                }
                if (metadatas != null) {
                    List<Map<String, Object>> metaList = new ArrayList<>();
                    for (Object m : metadatas) {
                        metaList.add((Map<String, Object>) m);
                    }
                    searchResult.setMetadatas(metaList);
                }
                if (distances != null) {
                    searchResult.setDistances(distances.toList(Double.class));
                }
                return searchResult;
            }
            log.warn("Search API 返回异常: {}", response.body());
        } catch (Exception e) {
            log.error("语义检索失败", e);
        }

        // 返回空结果
        return new SearchResult();
    }

    /**
     * 添加文档到知识库
     *
     * @param id      文档ID
     * @param content 文档内容
     * @param title   标题
     * @return 是否成功
     */
    public boolean addDocument(String id, String content, String title) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("id", id);
            requestBody.put("content", content);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("title", title);
            requestBody.put("metadata", metadata);

            HttpResponse response = HttpRequest.post(config.getBaseUrl() + "/add")
                    .header("Content-Type", "application/json")
                    .body(JSON.toJSONString(requestBody))
                    .timeout(config.getReadTimeout())
                    .execute();

            return response.isOk();
        } catch (Exception e) {
            log.error("添加文档失败", e);
            return false;
        }
    }

    /**
     * 健康检查
     */
    public boolean healthCheck() {
        try {
            HttpResponse response = HttpRequest.get(config.getBaseUrl() + "/health")
                    .timeout(config.getConnectTimeout())
                    .execute();
            return response.isOk();
        } catch (Exception e) {
            log.warn("Embedding服务健康检查失败", e);
            return false;
        }
    }

    /**
     * 生成 Mock 向量
     */
    private List<Double> generateMockEmbedding() {
        List<Double> mock = new ArrayList<>();
        for (int i = 0; i < 768; i++) {
            mock.add(Math.random() * 2 - 1);
        }
        return mock;
    }

    /**
     * 检索结果
     */
    @lombok.Data
    public static class SearchResult {
        private List<String> documents = new ArrayList<>();
        private List<Map<String, Object>> metadatas = new ArrayList<>();
        private List<Double> distances = new ArrayList<>();
    }
}
