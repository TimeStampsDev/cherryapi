package net.cherryflavor.api.spigot.command;

import net.cherryflavor.api.other.TabCommand;
import net.cherryflavor.api.other.help.HelpPageMaker;
import net.cherryflavor.api.spigot.ServerAPI;
import net.cherryflavor.api.spigot.player.OnlinePlayer;
import net.cherryflavor.api.tools.TextFormat;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2/20/2021
 * Time 12:32 AM
 */
public abstract class ServerCherryCommand implements CommandExecutor, TabCompleter {

    private String unknownCommand = "Unknown command. Type \"/help\" for help.";
    private String noPermission = ServerAPI.getAPI().getBasicMessages().getString("no-permission");

    private String command;
    private String permission;
    private String[] aliases;

    private boolean isCancelled;

    private ServerAPI serverAPI = ServerAPI.getAPI();

    private HelpPageMaker helpPage;

    private CommandSender sender;

    private List<TabCommand> tabCommandList;
    private List<TabCommand> consoleTabCommandList;

    //==================================================================================================================
    // CONSTRUCTORS
    //==================================================================================================================

    /**
     * @param isCancelled
     * @param command
     */
    public ServerCherryCommand(boolean isCancelled, String command) {
        this.isCancelled = isCancelled;
        this.command = command;
        this.permission = "";
        this.aliases = new String[] {""};
        tabCommandList = new ArrayList<>();
        consoleTabCommandList = new ArrayList<>();
        this.helpPage = new HelpPageMaker(getAPI());
    }

    /**
     * @param isCancelled
     * @param command
     * @param permission
     */
    public ServerCherryCommand(boolean isCancelled, String command, String permission) {
        this.isCancelled = isCancelled;
        this.command = command;
        this.permission = permission;
        this.aliases = new String[] {""};
        tabCommandList = new ArrayList<>();
        consoleTabCommandList = new ArrayList<>();
        this.helpPage = new HelpPageMaker(getAPI());
    }

    /**
     * @param isCancelled
     * @param command
     * @param permission
     * @param aliases
     */
    public ServerCherryCommand(boolean isCancelled, String command, String permission, String... aliases) {
        this.isCancelled = isCancelled;
        this.command = command;
        this.permission = permission;
        this.aliases = aliases;
        tabCommandList = new ArrayList<>();
        consoleTabCommandList = new ArrayList<>();
        this.helpPage = new HelpPageMaker(getAPI());
    }

    /**
     * @param isCancelled
     * @param command
     * @param aliases
     */
    public ServerCherryCommand(boolean isCancelled, String command, String... aliases) {
        this.isCancelled = isCancelled;
        this.command = command;
        this.permission = "";
        this.aliases = aliases;
        tabCommandList = new ArrayList<>();
        consoleTabCommandList = new ArrayList<>();
        this.helpPage = new HelpPageMaker(getAPI());
    }

    //==================================================================================================================
    // BOOLEAN METHODS
    //==================================================================================================================

    /**
     * Only executes if players perform the command
     * @param player
     * @param command
     * @param label
     * @param args
     * @return
     */
    public abstract boolean playerExecute(OnlinePlayer player, Command command, String label, String[] args);

    /**
     * Only executes if console perform the command
     * @param console
     * @param command
     * @param label
     * @param args
     * @return
     */
    public abstract boolean consoleExecute(CommandSender console, Command command, String label, String[] args);

    /**
     * @param commandSender
     * @param command
     * @param label
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        this.sender = commandSender;
        if (isCancelled == true) {
            commandSender.sendMessage(unknownCommand);
        } else  {
            if (commandSender instanceof Player) {
                OnlinePlayer player = new OnlinePlayer(((Player) commandSender).getUniqueId());
                if (this.permission.isEmpty()) {
                    if (args.length == 0) {
                        return playerExecute(player, command, label, args);
                    } else if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("help")) {
                            if (getHelpPage().getData().isEmpty()) {
                                sendColorfulMessage(getAPI().getBasicMessages().getString("help.no-help"));
                            } else {
                                sendColorfulMessage(getHelpPage().getPagePreview(0));
                            }
                        } else {
                            return playerExecute(player, command, label, args);
                        }
                    } else if (args.length == 2) {
                        if (args[0].equalsIgnoreCase("help")) {
                            try {
                                Integer page = Integer.parseInt(args[1])-1;

                                if (page > getHelpPage().getNumberOfPages()) {
                                    sendColorfulMessage(getAPI().getBasicMessages().getString("cannot-exceed-number-of-pages"));
                                } else {
                                    sendColorfulMessage(getHelpPage().getPagePreview(page));
                                }

                            } catch (NumberFormatException ex) {
                                sendColorfulMessage(getAPI().getBasicMessages().getString("invalid-number-message"));
                            }
                        } else {
                            return playerExecute(player, command, label, args);
                        }
                    } else {
                        return playerExecute(player, command, label, args);
                    }
                } else {
                    if (player.hasPermission(this.permission)) {
                        if (args.length == 0) {
                            return playerExecute(player, command, label, args);
                        } else if (args.length == 1) {
                            if (args[0].equalsIgnoreCase("help")) {
                                if (getHelpPage().getData().isEmpty()) {
                                    sendColorfulMessage(getAPI().getBasicMessages().getString("help.no-help"));
                                } else {
                                    sendColorfulMessage(getHelpPage().getPagePreview(0));
                                }
                            } else {
                                return playerExecute(player, command, label, args);
                            }
                        } else if (args.length == 2) {
                            if (args[0].equalsIgnoreCase("help")) {
                                try {
                                    Integer page = Integer.parseInt(args[1])-1;

                                    if (page > getHelpPage().getNumberOfPages()) {
                                        sendColorfulMessage(getAPI().getBasicMessages().getString("cannot-exceed-number-of-pages"));
                                    } else {
                                        sendColorfulMessage(getHelpPage().getPagePreview(page));
                                    }

                                } catch (NumberFormatException ex) {
                                    sendColorfulMessage(getAPI().getBasicMessages().getString("invalid-number-message"));
                                }
                            } else {
                                return playerExecute(player, command, label, args);
                            }
                        } else {
                            return playerExecute(player, command, label, args);
                        }
                    } else {
                        player.sendColorfulMessage(noPermission);
                    }
                }
            } else {
                if (args.length == 0) {
                    return consoleExecute(commandSender, command, label, args);
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("help")) {
                        if (getHelpPage().getData().isEmpty()) {
                            sendColorfulMessage(getAPI().getBasicMessages().getString("help.no-help"));
                        } else {
                            sendColorfulMessage(getHelpPage().getPagePreview(0));
                        }
                    } else {
                        return consoleExecute(commandSender, command, label, args);
                    }
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("help")) {
                        try {
                            Integer page = Integer.parseInt(args[1])-1;

                            if (page > getHelpPage().getNumberOfPages()) {
                                sendColorfulMessage(getAPI().getBasicMessages().getString("cannot-exceed-number-of-pages"));
                            } else {
                                sendColorfulMessage(getHelpPage().getPagePreview(page));
                            }

                        } catch (NumberFormatException ex) {
                            sendColorfulMessage(getAPI().getBasicMessages().getString("invalid-number-message"));
                        }
                    } else {
                        return consoleExecute(commandSender, command, label, args);
                    }
                } else {
                    return consoleExecute(commandSender, command, label, args);
                }
            }
        }
        return true;
    }

    /**
     * Method for when tab is pressed when performing commands
     * @param commandSender
     * @param command
     * @param label
     * @param args
     * @return
     */
    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            for (TabCommand tabCommand : tabCommandList) {
                if (args.length == tabCommand.getArgument()) {
                    if (!tabCommand.getWhenArgumentIs().isEmpty()) {
                        int arg = tabCommand.getArgument()-1;
                        if (args[arg].equalsIgnoreCase(tabCommand.getWhenArgumentIs())) {
                            return tabCommand.getTabList();
                        }
                    } else {
                        return tabCommand.getTabList();
                    }
                }
            }
        } else {
            for (TabCommand tabCommand : consoleTabCommandList) {
                if (args.length == tabCommand.getArgument()) {
                    if (!tabCommand.getWhenArgumentIs().isEmpty()) {
                        int arg = tabCommand.getArgument()-1;
                        if (args[arg].equalsIgnoreCase(tabCommand.getWhenArgumentIs())) {
                            return tabCommand.getTabList();
                        }
                    } else {
                        return tabCommand.getTabList();
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    //==================================================================================================================
    // GETTERS
    //==================================================================================================================

    /**
     * Returns API
     * @return
     */
    public ServerAPI getAPI() { return serverAPI; }

    /**
     * Get command
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gets permission for commmand
     * @return
     */
    public String getPermission() {
        return permission;
    }

    /**
     * Gets command aliases
     * @return
     */
    public String[] getAliases() {
        return aliases;
    }

    /**
     * Get tabcommands list for players
     * @return
     */
    public List<TabCommand> getTabCommandList() {
        return tabCommandList;
    }

    /**
     * Get tabcommands list for console
     * @return
     */
    public List<TabCommand> getConsoleTabCommandList() {
        return consoleTabCommandList;
    }

    /**
     *
     * @return
     */
    public boolean isCancelled() { return isCancelled; }

    /**
     * Help Page
     * @return
     */
    public HelpPageMaker getHelpPage() { return this.helpPage; }

    //==================================================================================================================
    // SETTERS
    //==================================================================================================================

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    //==================================================================================================================
    // METHODS
    //==================================================================================================================

    /**
     * Adds list to return for argument
     * @param argument
     * @param tabList
     */
    public void addTab(int argument, String whenArgumentIs, List<String> tabList) {
        TabCommand tabCommand = new TabCommand(argument, whenArgumentIs, tabList);
        tabCommandList.add(tabCommand);
    }

    /**
     * Removes list to return for argument
     * @param argument
     * @param tabList
     */
    public void removeTab(int argument, String whenArgumentIs, List<String> tabList) {
        TabCommand tabCommand = new TabCommand(argument, whenArgumentIs, tabList);
        tabCommandList.remove(tabCommand);
    }

    /**
     * Adds list to return for argument
     * @param argument
     * @param tabList
     */
    public void addConsoleTab(int argument, String whenArgumentIs, List<String> tabList) {
        TabCommand tabCommand = new TabCommand(argument, whenArgumentIs, tabList);
        consoleTabCommandList.add(tabCommand);
    }

    /**
     * Removes list to return for argument
     * @param argument
     * @param tabList
     */
    public void removeConsoleTab(int argument, String whenArgumentIs, List<String> tabList) {
        TabCommand tabCommand = new TabCommand(argument, whenArgumentIs, tabList);
        consoleTabCommandList.remove(tabCommand);
    }

    /**
     * Adds list to return for argument for both players and console
     * @param argument
     * @param tabList
     */
    public void addTabToBoth(int argument, String whenArgumentIs, List<String> tabList) {
        TabCommand tabCommand = new TabCommand(argument, whenArgumentIs, tabList);
        tabCommandList.add(tabCommand);
        consoleTabCommandList.add(tabCommand);
    }

    /**
     * Removes list to return for argument for both players and console
     * @param argument
     * @param tabList
     */
    public void removeTabToBoth(int argument, String whenArgumentIs, List<String> tabList) {
        TabCommand tabCommand = new TabCommand(argument, whenArgumentIs, tabList);
        tabCommandList.remove(tabCommand);
        consoleTabCommandList.remove(tabCommand);
    }

    /**
     * Sends commandSender a message
     * @param message
     */
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    /**
     * Sends commandSender a message
     * @param message
     */
    public void sendColorfulMessage(String... message) {
        for (String m : message) {
            sender.sendMessage(TextFormat.colorize(m));
        }
    }

    /**
     * Sends commandSender a message
     * @param message
     */
    public void sendColorfulMessage(List<String> message) {
        for (String m : message) {
            sender.sendMessage(TextFormat.colorize(m));
        }
    }

    public String correctCommas(String string) {
        String commaColor = getAPI().getBasicMessages().getString("comma-color-code");
        string.replace(",", commaColor + ",");
        return string;
    }

}
