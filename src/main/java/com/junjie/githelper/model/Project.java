package com.junjie.githelper.model;

public record Project(
    String id,
    String name,
    String path,
    String custom_prompt,
    String weekly_report_prompt
) {}
