/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2020
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package net.romvoid.discord;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.hooks.EventListener;
import net.romvoid.discord.command.CommandManager;
import net.romvoid.discord.commands.admin.CommandAutochannel;
import net.romvoid.discord.commands.admin.CommandPortal;
import net.romvoid.discord.commands.admin.CommandVerification;
import net.romvoid.discord.commands.botowner.CommandAlarm;
import net.romvoid.discord.commands.botowner.CommandCreateInvite;
import net.romvoid.discord.commands.botowner.CommandDBGuild;
import net.romvoid.discord.commands.botowner.CommandEval;
import net.romvoid.discord.commands.botowner.CommandGenerateDocsJSON;
import net.romvoid.discord.commands.botowner.CommandGlobalBlacklist;
import net.romvoid.discord.commands.botowner.CommandGuildData;
import net.romvoid.discord.commands.botowner.CommandGuilds;
import net.romvoid.discord.commands.botowner.CommandMaintenance;
import net.romvoid.discord.commands.botowner.CommandPlay;
import net.romvoid.discord.commands.botowner.CommandRestart;
import net.romvoid.discord.commands.botowner.CommandStop;
import net.romvoid.discord.commands.general.CommandFeedback;
import net.romvoid.discord.commands.general.CommandGitBug;
import net.romvoid.discord.commands.general.CommandHelp;
import net.romvoid.discord.commands.general.CommandInfo;
import net.romvoid.discord.commands.general.CommandInvite;
import net.romvoid.discord.commands.general.CommandUptime;
import net.romvoid.discord.commands.moderation.CommandMoveAll;
import net.romvoid.discord.commands.moderation.CommandNick;
import net.romvoid.discord.commands.moderation.CommandRole;
import net.romvoid.discord.commands.moderation.CommandSearch;
import net.romvoid.discord.commands.moderation.CommandServerInfo;
import net.romvoid.discord.commands.moderation.CommandUserInfo;
import net.romvoid.discord.commands.settings.CommandAutorole;
import net.romvoid.discord.commands.settings.CommandJoinMessage;
import net.romvoid.discord.commands.settings.CommandLeaveMessage;
import net.romvoid.discord.commands.settings.CommandLog;
import net.romvoid.discord.commands.settings.CommandPrefix;
import net.romvoid.discord.commands.settings.CommandWelcomeChannel;
import net.romvoid.discord.core.ListenerManager;
import net.romvoid.discord.features.GiveawayHandler;
import net.romvoid.discord.features.RemindHandler;
import net.romvoid.discord.features.VerificationKickHandler;
import net.romvoid.discord.features.VerificationUserHandler;
import net.romvoid.discord.permission.PermissionManager;
import net.romvoid.discord.sql.DatabaseManager;
import net.romvoid.discord.sql.GuildSQL;
import net.romvoid.discord.sql.MemberSQL;
import net.romvoid.discord.sql.MinecraftSQL;
import net.romvoid.discord.sql.MySQL;
import net.romvoid.discord.sql.PortalSQL;
import net.romvoid.discord.sql.ServerLogSQL;
import net.romvoid.discord.sql.UserSQL;
import net.romvoid.discord.sql.VerificationKickSQL;
import net.romvoid.discord.sql.VerificationUserSQL;
import net.romvoid.discord.sql.WarnSQL;
import net.romvoid.discord.util.Configuration;
import net.romvoid.discord.util.Info;
import net.romvoid.discord.util.Logger;
import net.romvoid.discord.util.Setup;

// TODO: Auto-generated Javadoc
/**
 * VoidBot main class. Initializes all components.
 *
 * @author ROMVoid
 */
public class VoidBot {
    
    /** The Constant timeStampFormatter. */
    private static final SimpleDateFormat timeStampFormatter = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");
    
    /** The Constant CONFIG_KEYS. */
    private static final String[] CONFIG_KEYS = {"token", "mysql_host", "mysql_port", "mysql_database", "mysql_password", "mysql_user", "git_token", "void_token", "maintenance"};
    
    /** The Constant dataFolder. */
    private static final String dataFolder = "data/";
    
    /** The instance. */
    private static VoidBot instance;
    
    /** The my SQL. */
    private final MySQL mySQL;
    
    /** The configuration. */
    private final Configuration configuration;
    
    /** The command manager. */
    private final CommandManager commandManager;
    
    /** The jda. */
    private JDA jda;
    
    /** The timer. */
    private final Timer timer;
    
    /** The event listeners. */
    private final Set<EventListener> eventListeners;
    
    /** The permission manager. */
    private final PermissionManager permissionManager;
    
    /** The database manager. */
    private final DatabaseManager databaseManager;

    /**
     * Constructs VoidBot.
     */
    private VoidBot() {
        instance = this;
        // initialize logger
        new File("thevoid_logs").mkdirs();
        Logger.logInFile(Info.BOT_NAME, Info.BOT_VERSION, "thevoid_logs/");

        timer = new Timer();
        eventListeners = new HashSet<>();
        databaseManager = new DatabaseManager();

        // load configuration and obtain missing config values
        new File(dataFolder).mkdirs();

        configuration = new Configuration(new File(Info.CONFIG_FILE));
        for (String configKey : CONFIG_KEYS) {
            if (!configuration.has(configKey)) {
                String input = Setup.prompt(configKey);
                configuration.set(configKey, input);
            }
        }

        // load MySQL adapter
        mySQL = new MySQL(Info.MYSQL_HOST, Info.MYSQL_PORT, Info.MYSQL_USER, Info.MYSQL_PASSWORD, Info.MYSQL_DATABASE);
        mySQL.connect();

        //Create databases if neccesary
        generateDatabases();


        commandManager = new CommandManager();
        registerCommandHandlers();
        permissionManager = new PermissionManager();
        // init JDA
        initJDA();

        // init features
        new GiveawayHandler();
        new RemindHandler();
        VerificationUserHandler.loadVerifyUser();
        VerificationKickHandler.loadVerifyKicks();

        String maintenanceStatus = getConfiguration().getString("maintenance");
        if (maintenanceStatus.equalsIgnoreCase("1")) {
            CommandMaintenance.enable();
        }
    }

    /**
     * Initializes the bot.
     *
     * @param args command line parameters.
     */
    public static void main(String[] args) {
        if (instance != null)
            throw new RuntimeException("VoidBot has already been initialized in this VM.");
        new VoidBot();
    }

    /**
     * Initializes the JDA instance.
     */
    public static void initJDA() {
        if (instance == null)
            throw new NullPointerException("VoidBot has not been initialized yet.");

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        builder.setToken(instance.configuration.getString("token"));
        builder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        // add all EventListeners
        for (EventListener listener : instance.eventListeners)
            builder.addEventListener(listener);

        new ListenerManager(builder);

        try {
            instance.jda = builder.build();
        } catch (LoginException e) {
            Logger.error(e.getMessage());
        }
        
        getJDA().getPresence().setStatus(OnlineStatus.ONLINE);

        Info.lastRestart = new Date();
    }

    /**
     * Registers all command handlers used in this project.
     *
     * @see CommandManager
     */
    private void registerCommandHandlers() {
        // Usage: commandManager.registerCommandHandler(yourCommandHandler...);

        // admin commands package
        commandManager.registerCommandHandlers(
                new CommandPortal(),
                new CommandVerification(),
                new CommandAutochannel(),
                new CommandRole()
        );
        // botowner commands package
        commandManager.registerCommandHandlers(
                new CommandDBGuild(),
                new CommandPlay(),
                new CommandRestart(),
                new CommandStop(),
                new CommandGuilds(),
                new CommandCreateInvite(),
                new CommandEval(),
                new CommandGlobalBlacklist(),
                new CommandGenerateDocsJSON(),
                new CommandMaintenance(),
                new CommandGuildData(),
                new CommandAlarm()
        );
        
//        commandManager.registerNewCommandHandlers(
//        	new CommandNewHandler()
//        	
//        	);
        
        // general commands package
        commandManager.registerCommandHandlers(
                new CommandHelp(),
                new CommandFeedback(),
                new CommandInfo(),
                new CommandInvite(),
                new CommandUptime(),
                new CommandGitBug()
        );
        
        // settings commands package
        commandManager.registerCommandHandlers(
                new CommandAutorole(),
                new CommandJoinMessage(),
                new CommandPrefix(),
                new CommandWelcomeChannel(),
                new CommandLeaveMessage(),
                new CommandLog()
        );
        // tools commands package
        commandManager.registerCommandHandlers(
                new CommandSearch(),
                new CommandServerInfo(),
                new CommandUserInfo(),
                new CommandMoveAll(),
                new CommandNick()
        );

        // also register commands from the old framework
        new CommandManager();
    }

    /**
     * Generate databases.
     */
    private void generateDatabases() {
        databaseManager.addGenerators(new ServerLogSQL(),
                new WarnSQL(),
                new MemberSQL(),
                new VerificationKickSQL(),
                new VerificationUserSQL(),
                new GuildSQL(),
                new UserSQL(),
                new PortalSQL(),
                new MinecraftSQL());

        databaseManager.generate();

    }

    /**
     * Gets the my SQL.
     *
     * @return the MySQL adapter.
     */
    public static MySQL getMySQL() {
        return instance == null ? null : instance.mySQL;
    }

    /**
     * Gets the configuration.
     *
     * @return the bot configuration.
     */
    public static Configuration getConfiguration() {
        return instance == null ? null : instance.configuration;
    }

    /**
     * Gets the jda.
     *
     * @return the JDA instance.
     */
    public static JDA getJDA() {
        return instance == null ? null : instance.jda;
    }

    /**
     * Gets the command manager.
     *
     * @return the CommandManager.
     */
    public static CommandManager getCommandManager() {
        return instance == null ? null : instance.commandManager;
    }

    /**
     * Gets the permission manager.
     *
     * @return the {@link PermissionManager}.
     */
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    /**
     * S get permission manager.
     *
     * @return the {@link PermissionManager} via a static reference.
     */
    public static PermissionManager sGetPermissionManager() {
        return instance == null ? null : instance.permissionManager;
    }

    /**
     * Gets the timer.
     *
     * @return a timer.
     */
    public static Timer getTimer() {
        return instance == null ? null : instance.timer;
    }

    /**
     * Adds an EventListener to the event pipe. EventListeners registered here will be re-registered when the JDA
     * instance is initialized again.
     *
     * @param listener the EventListener to register.
     * @return false if the bot has never been initialized or if the EventListener is already registered.
     */
    public static boolean registerEventListener(EventListener listener) {
        if (instance != null && instance.eventListeners.add(listener)) {
            if (instance.jda != null)
                instance.jda.addEventListener(listener);
            return true;
        }
        return false;
    }

    /**
     * Gets the new timestamp.
     *
     * @return a freshly generated timestamp in the 'dd.MM.yyyy HH:mm:ss' format.
     */
    public static String getNewTimestamp() {
        return timeStampFormatter.format(new Date());
    }

    /**
     * Gets the data folder.
     *
     * @return the data folder path
     */
    public static String getDataFolder() {
        return dataFolder;
    }

}