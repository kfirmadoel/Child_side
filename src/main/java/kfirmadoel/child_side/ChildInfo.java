package kfirmadoel.child_side;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "childs")
public class ChildInfo 
{
    @Id
    private String macAddr;
    private String ipAddr;

    public ChildInfo(String macAddr, String ipAddr) 
    {
        this.macAddr = macAddr;
        this.ipAddr = ipAddr;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

}
