package net.thevpc.halfa.config;

import net.thevpc.nuts.NId;
import net.thevpc.nuts.NLocations;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.NStoreType;
import net.thevpc.nuts.io.NPath;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonDocument;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

public class UserConfigManager {
    private NPath userConfigFile;
    private NSession session;

    public UserConfigManager(NSession session) {
        this.session = session;
        this.userConfigFile = NLocations.of().getStoreLocation(NId.of("net.thevpc.nuts:nuts").get(), NStoreType.CONF).resolve("user-config.tson");
    }


    public UserConfigs validate(UserConfigs config) {
        return (config == null ? new UserConfigs() : config).validate();
    }

    public UserConfig validate(UserConfig config) {
        return (config == null ? new UserConfig() : config).validate();
    }

    public UserConfigs updateUser(UserConfigs config, UserConfig user) {
        if (user != null) {
            validate(user);
            validate(config);
            config.updateUser(user);
        }
        return config;
    }

    public void saveUserConfig(UserConfig user) {
        UserConfigs userConfigs = loadUserConfigs();
        updateUser(userConfigs, user);
        saveUserConfigs(userConfigs);
        TsonElement elem = Tson.serializer().serialize(user);
        userConfigFile.mkParentDirs();
        try (OutputStream os = userConfigFile.getOutputStream()) {
            Tson.writer().write(os, elem);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public void saveUserConfigs(UserConfigs config) {
        config=validate(config);
        TsonElement elem = Tson.serializer().serialize(config);
        userConfigFile.mkParentDirs();
        try (OutputStream os = userConfigFile.getOutputStream()) {
            Tson.writer().write(os, elem);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public UserConfig loadUserConfig(String id) {
        return loadUserConfigs().findUser(id);
    }

    public UserConfigs loadUserConfigs() {
        UserConfigs config = null;
        if (userConfigFile.isRegularFile()) {
            try (InputStream is = userConfigFile.getInputStream()) {
                TsonDocument d = Tson.reader().readDocument(is);
                TsonSerializer serializer = Tson.serializer();
                config = serializer.deserialize(d.getContent(), UserConfigs.class);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
        config=validate(config);
        return config;
    }

    private int compare(Object a, Object b) {
        if (a == null && b == null) {
            return 0;
        }
        if (a == null && b != null) {
            return -1;
        }
        if (a != null && b == null) {
            return 1;
        }
        if (a instanceof Comparable) {
            return ((Comparable) a).compareTo(b);
        }
        throw new IllegalArgumentException("invalid comparision");
    }
}
