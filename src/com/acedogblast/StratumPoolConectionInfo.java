package com.acedogblast;
import java.net.InetAddress;
public class StratumPoolConectionInfo {
    private String hostname;
    private int hostport;
    private String username;
    private String password;

    public StratumPoolConectionInfo(String hostname, int port, String username, String password){
        this.hostname = hostname;
        this.hostport = port;
        this.username = username;
        this.password = password;
    }
    public String getHostname(){
        return hostname;
    }

    public int getHostport() {
        return hostport;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
