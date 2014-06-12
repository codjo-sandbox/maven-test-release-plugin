package net.codjo.maven.mojo.testrelease.unix;
import net.codjo.util.file.FileUtil;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
/**
 *
 */
public class UnixSessionFactory {
    public static final int DEFAULT_SSH_PORT = 22;
    private final String login;
    private final String host;
    private final URL privateKey;
    private final int port;


    public UnixSessionFactory(String login, String host, int port, URL privateKey) {
        this.login = login;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
    }


    public UnixSessionFactory(String login, String host, int port) {
        this(login, host, port, UnixSessionFactory.class.getResource("/META-INF/id_integration"));
    }


    public Session createSession() throws JSchException {
        JSch jsch = new JSch();

        try {
            File privateKeyFile = File.createTempFile("session", ".key");
            try {
                FileUtil.saveContent(privateKeyFile, FileUtil.loadContent(privateKey));
                jsch.addIdentity(privateKeyFile.getPath());
            }
            finally {
                privateKeyFile.delete();
            }
        }
        catch (IOException e) {
            throw new JSchException("impossible de cr�er la session", e);
        }

        Session session = jsch.getSession(login, host, port);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        return session;
    }


    public String getLogin() {
        return login;
    }


    public String getHost() {
        return host;
    }
}
