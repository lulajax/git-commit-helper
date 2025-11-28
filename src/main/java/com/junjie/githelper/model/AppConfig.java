package com.junjie.githelper.model;

import java.util.List;

public record AppConfig(
    String version,
    LLMSettings llm_settings,
    List<Project> projects,
    String selected_project_id,
    String weekly_report_prompt
) {
    // 默认构造器，提供默认的项目报告提示词
    public AppConfig(String version, LLMSettings llm_settings, List<Project> projects, String selected_project_id) {
        this(version, llm_settings, projects, selected_project_id, defaultWeeklyReportPrompt());
    }
    
    public static String defaultWeeklyReportPrompt() {
        return """
                Please generate a concise commit report based on the following Git commit logs.

                Requirements:

                - Summarize the main features and improvements completed this week.
                - List the key tasks.
                - Use professional technical language.
                - Format should be clear and organized.
                - Organize by functional modules.
                - in chinese

                Commit log:
                """;
    }
    
    public String getWeeklyReportPrompt() {
        return weekly_report_prompt != null ? weekly_report_prompt : defaultWeeklyReportPrompt();
    }
}
