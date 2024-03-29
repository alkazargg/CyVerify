package airio.cyverify.offline;

import airio.cyverify.main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;

import java.sql.*;

import static airio.cyverify.main.*;

public class offline implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] arg) {
        if(arg.length < 1){
            sendhelp((Player) sender);
            return false;
        }
        switch (arg[0]){
            case "start":{
                if(sender == Bukkit.getConsoleSender()){
                    sender.sendMessage("§c你控制台验证你妈正版呢???");
                    return true;
                }
                BukkitRunnable t = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            Connection connection = DriverManager.getConnection(jdbc_url, config.getString("mysql.username"),config.getString("mysql.password"));
                            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `cyverify` WHERE uuid=?");
                            statement.setString(1,sender.getName());
                            ResultSet set = statement.executeQuery();
                            if(!set.next()){
                                PreparedStatement addnew = connection.prepareStatement("INSERT INTO `cyverify`(`uuid`, `verified`,`locked`, `time`) VALUES (?,'0','0','0')");
                                addnew.setString(1,sender.getName());
                                addnew.executeUpdate();
                                Bukkit.getPlayer(sender.getName()).kickPlayer(config.getString("messages.start-verify-kick"));
                                addnew.close();
                            }else{
                                if(set.getBoolean("verified")){
                                    sender.sendMessage(config.getString("messages.verified"));
                                    dispatcher((Player) sender);
                                }else{
                                    Bukkit.getPlayer(sender.getName()).kickPlayer(config.getString("messages.start-verify-kick"));
                                }
                            }
                            statement.close();
                            connection.close();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                };
                t.runTaskLater(instance,1L);
                return true;
            }
            case "reload":{
                if(sender.hasPermission("cy.admin")){
                    config = instance.getConfig();
                    sender.sendMessage(ChatColor.WHITE + "Cy" + ChatColor.AQUA + "Verify" + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY +"§a成功重载");
                }else{
                    sender.sendMessage(ChatColor.WHITE + "Cy" + ChatColor.AQUA + "Verify" + ChatColor.DARK_GRAY + " » " + "§c你没有权限去这样做！");
                }
                return true;
            }
        }
        return false;
    }
    public static void dispatcher(Player p){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),config.getString("command_success").replace("%player%",p.getName()));
    }
    public void sendhelp(Player p){
        p.sendMessage("§f§m--------------------------------------------");
        p.sendMessage("§f");
        p.sendMessage(ChatColor.WHITE + "Cy" + ChatColor.AQUA + "Verify" + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "/cy start §f- 开始验证.");
        if(p.hasPermission("cy.all")){
            p.sendMessage(ChatColor.WHITE + "Cy" + ChatColor.AQUA + "Verify" + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "/cy reload §f- 重载配置文件.");
        }
        p.sendMessage("§f");
        p.sendMessage("§f§m--------------------------------------------");
    }

}
