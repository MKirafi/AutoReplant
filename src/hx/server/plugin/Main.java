package hx.server.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
public class Main extends JavaPlugin {
	
	private BlockBreakListener listener;
	private boolean enabled;

	@Override
	public void onEnable()
	{
		this.enabled = true;
		this.listener = new BlockBreakListener(this);
	    getServer().getPluginManager().registerEvents(listener, this);
	}
	
    @Override
    public void onDisable() {
    }

    
    private boolean checkCropExistence(String crop, CommandSender sender) {
    	if (!(crop.equals("wheat") ||
			  crop.equals("carrots") ||
			  crop.equals("potatoes") ||
			  crop.equals("beetroot") ||
			  crop.equals("nether wart"))) {
    		return false;
		}
    	return true;
    }
    
    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
    	if (command.getName().equalsIgnoreCase("autoreplant")) {
    		if (args[0].equals("enable")) {
    			listener.enabled = true;
    			sender.sendMessage("Autoreplant enabled.");
    			return true;
    		} else if (args[0].equals("disable")){
    			listener.enabled = false;
    			sender.sendMessage("Autoreplant disabled.");
    			return true;
    		}
    	}
    	
    	if (command.getName().equalsIgnoreCase("setdelay")) {
    		if(args.length != 1) {
    			return false;
    		}
    		float delay = Float.parseFloat(args[0]);
    		listener.setdelay(delay);
    		sender.sendMessage("Delay set to " + delay + " seconds.");
    		return true;
    	}
    	
    	if(command.getName().equalsIgnoreCase("removecrop")) {
    		if(!checkCropExistence(args[0], sender) || args.length != 1) {
    			return false;
    		}
    		listener.removeCrop(args[0]);
    		sender.sendMessage("Removed the crop " + args[0] + " from the autoreplant list.");
    		return true;
    	}
    	
    	if(command.getName().equalsIgnoreCase("addcrop")) {
    		if(!checkCropExistence(args[0], sender) || args.length != 1) {
    			return false;
    		}
    		listener.addCrop(args[0]);
    		sender.sendMessage("Added the crop " + args[0] + " to the autoreplant list.");
    		return true;
    	}
    	
    	if(command.getName().equalsIgnoreCase("adjustdrops")) {
    		if(!checkCropExistence(args[0], sender) || args.length != 1) {
    			return false;
    		}
    		listener.adjustdrops = Boolean.parseBoolean(args[0]);
    		sender.sendMessage("Ajusting the crops: " + args[0]);
    		return true;
    	}

        return false;
    }

    
    
}
