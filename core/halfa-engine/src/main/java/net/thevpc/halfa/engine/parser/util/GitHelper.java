package net.thevpc.halfa.engine.parser.util;

import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.nuts.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.spi.NScopeType;
import net.thevpc.nuts.time.NChronometer;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitHelper {
    public static boolean isGithubFolder(String sp) {
        return
                //  github://thevpc/halfa-templates/myFolder
                sp.startsWith("github://")
                        // git@github.com:thevpc/halfa-templates.git/myFolder
                        || sp.startsWith("git@")
                        // https://github.com/thevpc/halfa-templates.git/myFolder
                        || sp.startsWith("https://github.com/")
                ;
    }

    public static NPath resolveGithubPath(String githubPath, HMessageList messages, NSession session) {
        NPath userConfHome;
        NPath appCacheFolder = session.getAppCacheFolder();
        if (appCacheFolder == null) {
            userConfHome = NLocations.of(session).getStoreLocation(NId.of("net.thevpc.halfa:halfa").get(), NStoreType.CACHE).resolve("github");
        } else {
            userConfHome = appCacheFolder.resolve("halfa/github");
        }
        if (githubPath.startsWith("github://")) {
            Pattern pattern = Pattern.compile("github://(?<user>[a-zA-Z0-9_-]+)/(?<repo>[a-zA-Z0-9_-]+)((/(?<path>.*))?)");
            Matcher matcher = pattern.matcher(githubPath);
            if (matcher.matches()) {
                String user = matcher.group("user");
                String repo = matcher.group("repo");
                String path = NStringUtils.trim(matcher.group("path"));
                cloneOrPull(userConfHome, user, repo, "git@github.com:" + user + "/" + repo + ".git", messages, session);
                return userConfHome.resolve(user + "/" + repo + "/" + path);
            }
        } else if (githubPath.startsWith("git@")) {
            Pattern pattern = Pattern.compile("git@github.com:(?<user>[a-zA-Z0-9_-]+)/(?<repo>[a-zA-Z0-9_-]+).git((/(?<path>.*))?)");
            Matcher matcher = pattern.matcher(githubPath);
            if (matcher.matches()) {
                String user = matcher.group("user");
                String repo = matcher.group("repo");
                String path = NStringUtils.trim(matcher.group("path"));
                cloneOrPull(userConfHome, user, repo, "git@github.com:" + user + "/" + repo + ".git", messages, session);
                return userConfHome.resolve(user + "/" + repo + "/" + path);
            }
        } else if (githubPath.startsWith("https://github.com")) {
            // https://github.com/thevpc/halfa-templates.git
            Pattern pattern = Pattern.compile("https://github.com/(?<user>[a-zA-Z0-9_-]+)/(?<repo>[a-zA-Z0-9_-]+).git((/(?<path>.*))?)");
            Matcher matcher = pattern.matcher(githubPath);
            if (matcher.matches()) {
                String user = matcher.group("user");
                String repo = matcher.group("repo");
                String path = NStringUtils.trim(matcher.group("path"));
                cloneOrPull(userConfHome, user, repo, "https://github.com/" + user + "/" + repo + ".git", messages, session);
                return userConfHome.resolve(user + "/" + repo + "/" + path);
            }
        }
        throw new IllegalArgumentException("invalid github path : " + githubPath);
    }

    private static void cloneOrPull(NPath userConfHome, String user, String repo, String githubPath, HMessageList messages, NSession session) {
        userConfHome.resolve(user).mkdirs();
        NPath localRepo = userConfHome.resolve(user).resolve(repo);
        boolean pulling = false;
        boolean succeeded = false;
        String errorMessage = null;
        NChronometer c = NChronometer.startNow();
        try {
            if (localRepo.isDirectory()) {
                Instant now = Instant.now();
                Instant last = (Instant) session.getProperty("resolveGithubPath.lastPull",NScopeType.WORKSPACE).orNull();
                if (last == null || now.toEpochMilli() - last.toEpochMilli() > (1000 * 60 * 5)) {
                    pulling = true;
                    NExecCmd.of(session)
                            .addCommand("git", "pull")
                            .setDirectory(localRepo)
                            .failFast()
                            .run();
                } else {
                    NMsg message = NMsg.ofC("ignored pull repo %s to %s", NPath.of(githubPath, session), localRepo);
                    if (messages != null) {
                        messages.addWarning(message);
                    }
                    if (session.isTrace()) {
                        session.out().println(message);
                    }
                }
                session.setProperty("resolveGithubPath.lastPull", NScopeType.WORKSPACE, now);
            } else {
                NExecCmd.of(session)
                        .addCommand("git", "clone", githubPath)
                        .setDirectory(userConfHome.resolve(user))
                        .failFast()
                        .run();
            }
            succeeded = true;
        } catch (RuntimeException e) {
            errorMessage = e.getMessage();
            throw e;
        } finally {
            c.stop();
            if (!succeeded) {
                NMsg message = NMsg.ofC("took %s and failed to %s repo %s to %s : %s", c, pulling ? "pull" : "clone", NPath.of(githubPath, session), localRepo, errorMessage);
                if (messages != null) {
                    messages.addError(message);
                }
                if (session.isTrace()) {
                    session.out().println(message);
                }
            } else {
                NMsg message = NMsg.ofC("took %s to %s repo %s to %s", c, pulling ? "pull" : "clone", NPath.of(githubPath, session), localRepo);
                if (messages != null) {
                    messages.addWarning(message);
                }
                if (session.isTrace()) {
                    session.out().println(message);
                }
            }
        }
    }
}
