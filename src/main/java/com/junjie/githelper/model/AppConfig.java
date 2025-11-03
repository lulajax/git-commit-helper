package com.junjie.githelper.model;

import java.util.List;

public record AppConfig(
    String version,
    LLMSettings llm_settings,
    List<Project> projects,
    String selected_project_id,
    String weekly_report_prompt
) {
    // 默认构造器，提供默认的周报提示词
    public AppConfig(String version, LLMSettings llm_settings, List<Project> projects, String selected_project_id) {
        this(version, llm_settings, projects, selected_project_id, getDefaultWeeklyReportPrompt());
    }
    
    private static String getDefaultWeeklyReportPrompt() {
        return """
                请根据以下 Git 提交日志，生成一份简洁的研发周报。
                
                要求：
                1. 总结本周主要完成的功能和改进
                2. 列出重点工作项
                3. 使用专业的技术语言
                4. 格式清晰，条理分明
                5. 突出技术亮点和创新点
                6. 按功能模块分类整理
                
                提交日志：
                """;
    }
    
    public String getWeeklyReportPrompt() {
        return weekly_report_prompt != null ? weekly_report_prompt : getDefaultWeeklyReportPrompt();
    }
}
