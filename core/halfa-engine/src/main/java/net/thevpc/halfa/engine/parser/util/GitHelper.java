package net.thevpc.halfa.engine.parser.util;

import net.thevpc.nuts.*;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NStringUtils;

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

    public static NPath resolveGithubPath(String githubPath, NSession session) {
        NPath userConfHome;
        NPath appCacheFolder = session.getAppCacheFolder();
        if(appCacheFolder==null){
            userConfHome=NLocations.of(session).getStoreLocation(NId.API_ID, NStoreType.CACHE).resolve("halfa/github");
        }else{
            userConfHome=appCacheFolder.resolve("halfa/github");
        }
        if (githubPath.startsWith("github://")) {
            Pattern pattern = Pattern.compile("github://(?<user>[a-zA-Z0-9_-]+)/(?<repo>[a-zA-Z0-9_-]+)((/(?<path>.*))?)");
            Matcher matcher = pattern.matcher(githubPath);
            if (matcher.matches()) {
                String user = matcher.group("user");
                String repo = matcher.group("repo");
                String path = NStringUtils.trim(matcher.group("path"));
                NPath localRepo = userConfHome.resolve(user + "/" + repo + "/" + path);
                userConfHome.resolve(user).mkdirs();
                if (localRepo.isDirectory()) {
                    NExecCmd.of(session)
                            .addCommand("git", "pull")
                            .setDirectory(userConfHome.resolve(user + "/" + repo))
                            .failFast()
                            .run();
                } else {
                    NExecCmd.of(session)
                            .addCommand("git", "clone", "git@github.com:" + user + "/" + repo + ".git")
                            .setDirectory(userConfHome.resolve(user))
                            .failFast()
                            .run();
                }
                return localRepo;
            }
        } else if (githubPath.startsWith("git@")) {
            Pattern pattern = Pattern.compile("git@github.com:(?<user>[a-zA-Z0-9_-]+)/(?<repo>[a-zA-Z0-9_-]+).git((/(?<path>.*))?)");
            Matcher matcher = pattern.matcher(githubPath);
            if (matcher.matches()) {
                String user = matcher.group("user");
                String repo = matcher.group("repo");
                String path = NStringUtils.trim(matcher.group("path"));
                NPath localRepo = userConfHome.resolve(user + "/" + repo + "/" + path);
                userConfHome.resolve(user).mkdirs();
                if (localRepo.isDirectory()) {
                    NExecCmd.of(session)
                            .addCommand("git", "pull")
                            .setDirectory(userConfHome.resolve(user + "/" + repo))
                            .failFast()
                            .run();
                } else {
                    NExecCmd.of(session)
                            .addCommand("git", "clone", "git@github.com:" + user + "/" + repo + ".git")
                            .setDirectory(userConfHome.resolve(user))
                            .failFast()
                            .run();
                }
                return localRepo;
            }
        } else if (githubPath.startsWith("https://github.com")) {
            // https://github.com/thevpc/halfa-templates.git
            Pattern pattern = Pattern.compile("https://github.com/(?<user>[a-zA-Z0-9_-]+)/(?<repo>[a-zA-Z0-9_-]+).git((/(?<path>.*))?)");
            Matcher matcher = pattern.matcher(githubPath);
            if (matcher.matches()) {
                String user = matcher.group("user");
                String repo = matcher.group("repo");
                String path = NStringUtils.trim(matcher.group("path"));
                NPath localRepo = userConfHome.resolve(user + "/" + repo + "/" + path);
                userConfHome.resolve(user).mkdirs();
                if (localRepo.isDirectory()) {
                    NExecCmd.of(session)
                            .addCommand("git", "pull")
                            .setDirectory(userConfHome.resolve(user + "/" + repo))
                            .failFast()
                            .run();
                } else {
                    NExecCmd.of(session)
                            .addCommand("git", "clone", "https://github.com/" + user + "/" + repo + ".git")
                            .setDirectory(userConfHome.resolve(user))
                            .failFast()
                            .run();
                }
                return localRepo;
            }
        }
        throw new IllegalArgumentException("invalid github path : " + githubPath);
    }
}
