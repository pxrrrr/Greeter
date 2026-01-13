package net.pxr.greeter;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.simple.BooleanCodec;
import com.hypixel.hytale.codec.codecs.simple.StringCodec;

public class GreeterConfig {

    public static final BuilderCodec<GreeterConfig> CODEC = BuilderCodec.builder(GreeterConfig.class, GreeterConfig::new)
        .append(new KeyedCodec<Boolean>("EnableJoinMessages", new BooleanCodec()), GreeterConfig::setEnableJoinMessages, GreeterConfig::isEnableJoinMessages).add()
        .append(new KeyedCodec<Boolean>("EnableLeaveMessages", new BooleanCodec()), GreeterConfig::setEnableLeaveMessages, GreeterConfig::isEnableLeaveMessages).add()
        .append(new KeyedCodec<Boolean>("EnableFirstTimeWelcome", new BooleanCodec()), GreeterConfig::setEnableFirstTimeWelcome, GreeterConfig::isEnableFirstTimeWelcome).add()
        .append(new KeyedCodec<Boolean>("SuppressNativeWelcome", new BooleanCodec()), GreeterConfig::setSuppressNativeWelcome, GreeterConfig::isSuppressNativeWelcome).add()
        .append(new KeyedCodec<String>("WelcomeMessage", new StringCodec()), GreeterConfig::setWelcomeMessage, GreeterConfig::getWelcomeMessage).add()
        .append(new KeyedCodec<String>("FirstTimeMessage", new StringCodec()), GreeterConfig::setFirstTimeMessage, GreeterConfig::getFirstTimeMessage).add()
        .append(new KeyedCodec<String>("ReturnMessage", new StringCodec()), GreeterConfig::setReturnMessage, GreeterConfig::getReturnMessage).add()
        .append(new KeyedCodec<String>("NewPlayerBroadcast", new StringCodec()), GreeterConfig::setNewPlayerBroadcast, GreeterConfig::getNewPlayerBroadcast).add()
        .append(new KeyedCodec<String>("JoinBroadcast", new StringCodec()), GreeterConfig::setJoinBroadcast, GreeterConfig::getJoinBroadcast).add()
        .append(new KeyedCodec<String>("LeaveBroadcast", new StringCodec()), GreeterConfig::setLeaveBroadcast, GreeterConfig::getLeaveBroadcast).add()
        .build();

    private boolean enableJoinMessages = true;
    private boolean enableLeaveMessages = true;
    private boolean enableFirstTimeWelcome = true;
    private boolean suppressNativeWelcome = false;
    private String welcomeMessage = "*** WELCOME TO THE SERVER! ***";
    private String firstTimeMessage = "This is your first time here!";
    private String returnMessage = "Welcome back!";
    private String newPlayerBroadcast = "[NEW] {player} has joined for the first time!";
    private String joinBroadcast = "[+] {player} has joined.";
    private String leaveBroadcast = "[-] {player} has left.";

    public GreeterConfig() {}

    public boolean isEnableJoinMessages() { return enableJoinMessages; }
    public boolean isEnableLeaveMessages() { return enableLeaveMessages; }
    public boolean isEnableFirstTimeWelcome() { return enableFirstTimeWelcome; }
    public boolean isSuppressNativeWelcome() { return suppressNativeWelcome; }
    public String getWelcomeMessage() { return welcomeMessage; }
    public String getFirstTimeMessage() { return firstTimeMessage; }
    public String getReturnMessage() { return returnMessage; }
    public String getNewPlayerBroadcast() { return newPlayerBroadcast; }
    public String getJoinBroadcast() { return joinBroadcast; }
    public String getLeaveBroadcast() { return leaveBroadcast; }

    public void setEnableJoinMessages(boolean v) { this.enableJoinMessages = v; }
    public void setEnableLeaveMessages(boolean v) { this.enableLeaveMessages = v; }
    public void setEnableFirstTimeWelcome(boolean v) { this.enableFirstTimeWelcome = v; }
    public void setSuppressNativeWelcome(boolean v) { this.suppressNativeWelcome = v; }
    public void setWelcomeMessage(String v) { this.welcomeMessage = v; }
    public void setFirstTimeMessage(String v) { this.firstTimeMessage = v; }
    public void setReturnMessage(String v) { this.returnMessage = v; }
    public void setNewPlayerBroadcast(String v) { this.newPlayerBroadcast = v; }
    public void setJoinBroadcast(String v) { this.joinBroadcast = v; }
    public void setLeaveBroadcast(String v) { this.leaveBroadcast = v; }
}
