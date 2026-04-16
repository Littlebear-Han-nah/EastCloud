package com.EastCloud.smart_cs.service;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class QwenAiService {

    @Value("${qwen.api.key}")
    private String apiKey;

    @Value("${qwen.api.url}")
    private String baseUrl;

    @Value("${qwen.model.name}")
    private String modelName;

    private ChatLanguageModel chatModel;
    
    @org.springframework.beans.factory.annotation.Autowired
    private RAGService ragService;

    @PostConstruct
    public void init() {
        // 使用 LangChain4j 初始化大模型客户端
        chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .build();
    }

    // 核心业务：结合知识库上下文生成草稿
    public String generateDraftWithRAG(String buyerMessage) {
        // 检索前 3 个最相关的段落
        var matches = ragService.search(buyerMessage, 3);
        StringBuilder contextBuilder = new StringBuilder();
        for (var match : matches) {
            contextBuilder.append(match.embedded().text()).append("\n");
        }
        
        String context = contextBuilder.toString();
        
        String prompt = "你是一个专业的亚马逊跨境电商客服。买家发来消息：\n" + buyerMessage + 
                        "\n\n请参考以下我们的产品知识库内容：\n" + context +
                        "\n\n请用极其地道、专业的商务英语回复买家。如果买家遇到问题，请带有一点同理心（Empathy），安抚客户并提供相应方案。请直接输出英文邮件正文，不需要任何前缀说明。";
        return chatModel.generate(prompt);
    }
}
