package com.junjie.githelper.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.junjie.githelper.model.Project;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class GitService {

    public String getStagedChanges(Project project) throws IOException, GitAPIException {
        File repoDir = new File(project.path());
        
        try (Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(repoDir, ".git"))
                .build();
             Git git = new Git(repository)) {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            git.diff()
               .setCached(true)
               .setOutputStream(outputStream)
               .call();
            
            return outputStream.toString();
        }
    }

    public String getRecentCommitMessages(Project project) throws IOException, GitAPIException {
        File repoDir = new File(project.path());

        try (Repository repository = new FileRepositoryBuilder()
            .setGitDir(new File(repoDir, ".git"))
            .build();
             Git git = new Git(repository)) {

            Iterable<RevCommit> commits = git.log().setMaxCount(5).call();
            return StreamSupport.stream(commits.spliterator(), false)
                .map(RevCommit::getShortMessage)
                .collect(Collectors.joining("\n"));
        }
    }

    public void commit(Project project, String message) throws IOException, GitAPIException {
        File repoDir = new File(project.path());

        try (Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(repoDir, ".git"))
                .build();
             Git git = new Git(repository)) {

            git.commit().setMessage(message).call();
        }
    }
    
    /**
     * 获取指定时间段内的提交日志（包含代码变更）
     * @param project 项目
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param includeDiff 是否包含代码变更详情
     * @return 格式化的提交日志
     */
    public String getCommitLogs(Project project, LocalDate startDate, LocalDate endDate, boolean includeDiff) throws IOException, GitAPIException {
        File repoDir = new File(project.path());
        
        // 转换 LocalDate 到 Date
        Date since = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date until = Date.from(endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        
        try (Repository repository = new FileRepositoryBuilder()
                .setGitDir(new File(repoDir, ".git"))
                .build();
             Git git = new Git(repository)) {
            
            Iterable<RevCommit> commits = git.log().call();
            StringBuilder logBuilder = new StringBuilder();
            int count = 0;
            
            for (RevCommit commit : commits) {
                Date commitDate = new Date(commit.getCommitTime() * 1000L);
                
                // 检查提交是否在指定时间范围内
                if (commitDate.after(since) && commitDate.before(until)) {
                    count++;
                    Instant instant = Instant.ofEpochSecond(commit.getCommitTime());
                    LocalDate commitLocalDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                    
                    logBuilder.append("=".repeat(80)).append("\n");
                    logBuilder.append("提交: ").append(commit.getName().substring(0, 8)).append("\n");
                    logBuilder.append("作者: ").append(commit.getAuthorIdent().getName()).append("\n");
                    logBuilder.append("日期: ").append(commitLocalDate).append("\n");
                    logBuilder.append("信息: ").append(commit.getFullMessage()).append("\n");
                    
                    // 如果需要包含 diff
                    if (includeDiff) {
                        try {
                            ByteArrayOutputStream diffStream = new ByteArrayOutputStream();
                            
                            // 获取父提交
                            if (commit.getParentCount() > 0) {
                                RevCommit parent = commit.getParent(0);
                                git.diff()
                                   .setOldTree(prepareTreeParser(repository, parent))
                                   .setNewTree(prepareTreeParser(repository, commit))
                                   .setOutputStream(diffStream)
                                   .call();
                                
                                String diff = diffStream.toString();
                                if (!diff.isEmpty()) {
                                    logBuilder.append("\n代码变更:\n");
                                    logBuilder.append(diff);
                                }
                            } else {
                                // 初始提交，显示所有文件
                                logBuilder.append("\n[初始提交 - 显示所有新增文件]\n");
                            }
                        } catch (Exception e) {
                            logBuilder.append("\n[无法获取此提交的代码变更: ").append(e.getMessage()).append("]\n");
                        }
                    }
                    
                    logBuilder.append("\n");
                }
            }
            
            if (count == 0) {
                return "在 " + startDate + " 至 " + endDate + " 期间没有找到提交记录。";
            }
            
            String header = String.format("共找到 %d 条提交记录 (%s 至 %s)\n\n", count, startDate, endDate);
            return header + logBuilder.toString();
        }
    }
    
    /**
     * 兼容旧方法 - 不包含 diff
     */
    public String getCommitLogs(Project project, LocalDate startDate, LocalDate endDate) throws IOException, GitAPIException {
        return getCommitLogs(project, startDate, endDate, false);
    }
    
    /**
     * 准备 TreeParser 用于 diff
     */
    private org.eclipse.jgit.treewalk.AbstractTreeIterator prepareTreeParser(Repository repository, RevCommit commit) throws IOException {
        try (org.eclipse.jgit.revwalk.RevWalk walk = new org.eclipse.jgit.revwalk.RevWalk(repository)) {
            RevCommit parsedCommit = walk.parseCommit(commit.getId());
            org.eclipse.jgit.treewalk.CanonicalTreeParser treeParser = new org.eclipse.jgit.treewalk.CanonicalTreeParser();
            try (org.eclipse.jgit.lib.ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, parsedCommit.getTree().getId());
            }
            return treeParser;
        }
    }
}
