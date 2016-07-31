package irc.examples.welcomebot;

import java.util.concurrent.CompletableFuture;

public class IrcBot {

  public static void main(String[] args) {
    // Sets the basic IRC Client configuration.
    IrcConnector connector = IrcConnector.newBuilder().setServer("irc.mibbit.net")
        .setChannel("#pw")
        .build();

    // Connects to the server and gets the IRC Client future.
    IrcClient client = connector.connect().get();

    // Sends "Hi all!"  to channel #pw.
    client.sendTo("#pw", "Hi all!");

    // Joins a channel and leaves it as soon as it joins it asynchronously.
    CompletableFuture<IrcChannel> channelFuture = client.join("#guiamt");
    channelFuture.thenAccept((IrcChannel channel) -> channel.leave("Sorry! Wrong channel :P"));

    client.addListener(new IrcBotWelcomeListener(client));


    // Blocks and waits until the connection is closed.
    client.waitUntilClosed();

    // The client is now disconnected.
  }

  private static class IrcBotWelcomeListener extends IrcEventAdapter {

    private final IrcClient client;

    IrcBotWelcomeListener(IrcClient client) {
      this.client = client;
    }

    @Override
    public void onJoin(IrcChannel channel, IrcUser user) {
      channel.send("Hello " + user.getNick() + " :)");
    }

    @Override
    public void onPrivateMessage(IrcUser user, String message) {
      if (message.equals("!quit")) {
        client.quit("Goodbye!");
      }
    }
  }
}
