package airio.cyverify;

import airio.cyverify.offline.offline;
import airio.cyverify.offline.offlineListener;
import airio.cyverify.online.ActDisabler;
import airio.cyverify.online.online;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class main extends JavaPlugin {
    public static JavaPlugin instance;
    public static FileConfiguration config;
    public static String jdbc_url;
    @Override
    public void onEnable(){
        saveDefaultConfig();
        getLogger().info("============================================");
        getLogger().info(ChatColor.AQUA  + "CyVerify" + ChatColor.DARK_AQUA + "   Cyttt");
        getLogger().info("插件版本:" + this.getDescription().getVersion());
        getLogger().info("============================================");
        config = this.getConfig();
        getLogger().info("测试数据库..");
        instance = this;
        jdbc_url = "jdbc:mysql://"+config.getString("mysql.ip")+":"+config.getString("mysql.port")+"/"+config.getString("mysql.dbname");
        try {
            Connection connection = DriverManager.getConnection(jdbc_url, config.getString("mysql.username"),config.getString("mysql.password"));
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `cyverify` ( `uuid` MEDIUMTEXT NOT NULL , `verified` BOOLEAN NOT NULL , `locked` BOOLEAN NOT NULL , `time` BIGINT NOT NULL ) ENGINE = InnoDB;");
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            getLogger().warning("数据库错误，请检查配置文件。。");
        }
        if(getServer().getOnlineMode()){
            getLogger().info("当前模式:" + ChatColor.AQUA + "正版模式");
            getLogger().info("注册监听器...");
            Bukkit.getPluginManager().registerEvents(new ActDisabler(), this);
            Bukkit.getPluginManager().registerEvents(new online(), this);
        }else{
            getLogger().info("当前模式:" + ChatColor.YELLOW + "离线模式");
            getLogger().info("注册命令处理器...");
            Bukkit.getPluginManager().registerEvents(new offlineListener(), this);
            if(Bukkit.getPluginCommand("cy") != null){
                Bukkit.getPluginCommand("cy").setExecutor(new offline());
            }

        }
        getLogger().info("注册成功, 插件已启动");
    }
}
