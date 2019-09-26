package hx.server.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BlockBreakListener implements Listener{

	public boolean adjustdrops;
	public boolean enabled;
	
	private Plugin plugin;
	private int delay;
	private Map<String, Material> crop_mapping;
	
	public BlockBreakListener(Plugin plugin) {
		this.plugin = plugin;
		this.adjustdrops = true;
		this.delay = 10;
		this.crop_mapping = new HashMap<String, Material>();
		this.crop_mapping.put("wheat", Material.WHEAT);
		this.crop_mapping.put("carrots", Material.CARROTS);
		this.crop_mapping.put("potatoes", Material.POTATOES);
		this.crop_mapping.put("beetroot", Material.BEETROOTS);
		this.crop_mapping.put("nether wart", Material.NETHER_WART);
	}
	
	public void setdelay(float delay) {
		this.delay = (int) (delay * 20);
	}
	
	public void removeCrop(String crop) {
		this.crop_mapping.remove(crop);
	}
	
	public void addCrop(String crop) {
		if (crop.equals("wheat")) { this.crop_mapping.put(crop, Material.WHEAT); }
		if (crop.equals("carrots")) { this.crop_mapping.put(crop, Material.CARROTS); }
		if (crop.equals("potatoes")) { this.crop_mapping.put(crop, Material.POTATOES); }
		if (crop.equals("beetroot")) { this.crop_mapping.put(crop, Material.BEETROOTS); }
		if (crop.equals("nether wart")) { this.crop_mapping.put(crop, Material.NETHER_WART); }
	}
	
	@EventHandler
    private void onBlockBreak(final BlockBreakEvent e) {
		if (!enabled) {
			return;
		}
    	try {
    		//	Define a list of materials which are replaced when broken (crops).    	
    		List<Material> crops = new ArrayList<Material>(this.crop_mapping.values());
//    		List<Material> crops = Arrays.asList(Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS, Material.NETHER_WART);
    		
    		// Define a number of variables used later.	    	
    		Block block = e.getBlock();
	    	Material material = block.getType();
	    	World world = block.getWorld();
	    	Collection<ItemStack> drops = block.getDrops();
	    	Iterator<ItemStack >iterator = drops.iterator();
	    	
	    	//	Update the drops so one of the seeds (wheat seeds, carrots, potatoes etc.) is taken up by replanting.    	
	    	if (adjustdrops) {
	    		drops = updateDrops(drops, iterator, crops);
	    	}

	    	//	Only replace if the block broken is a crop in the crops list.    	
	    	if (crops.contains(material)) {
		    	Ageable crop = (Ageable) block.getBlockData();
	    	
		    	//	Only replace if the crop is of maximum age.
				if (crop.getAge() != crop.getMaximumAge()) {
					return;	
				}
				
				// Execute the drops of the broken crop.
	    		while (iterator.hasNext()) {
	    			ItemStack item = iterator.next();
	    			world.dropItemNaturally(e.getBlock().getLocation(), item);
	    		}
	    		
	    		// Schedule the replacing for the block to be delayed. If this is
	    		// not done, and you use block.setType() normally, the block never gets
	    		// replaced since the BlockBreakEvent simply overrides it and replaces it
	    		// with air again. Replace after 10 ticks (0.5 second) so the crops don't 
	    		// immediately get broken when holding down the mouse button.
	    		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	    			  @Override
	    			  public void run() {
	    			    block.setType(material);
	    			  }
	    			}, this.delay);
    		}
    	// Catch any errors.	    	
    	} catch(Exception exception) {
    		System.out.println(exception.toString());
    	}
	}
	
	// Function to update the drops of the crops such that one of the seeds is taken.	
	private Collection<ItemStack> updateDrops(Collection<ItemStack> drops, Iterator<ItemStack> iterator, List<Material> crops) {
		// Loop through all the ItemStacks of an item (i.e. wheat, wheat_seeds)
		while(iterator.hasNext()) {
    		ItemStack stack = iterator.next();
    		Material material = stack.getType();
    		// Don't do anything if the drop is the actual beetroot or wheat item.
    		if (material == Material.BEETROOTS || material == Material.WHEAT) {
    			continue;
			}
    		// Since wheat and beetroot are not the drops you actually replant, we have to
    		// specifically look for their seeds rather than the item in the crops list.
    		if (material == Material.BEETROOT_SEEDS || material == Material.WHEAT_SEEDS) {
    			stack.setAmount(stack.getAmount() - 1);
    			// Once the seeds have been found, no need to look further.
    			break;
    		}
    		if (crops.contains(material)) {
    			
    			stack.setAmount(stack.getAmount() - 1);
    			break;
    		}
    	}
		
		// Return the drops to update (pass by value).
		return drops;
	}
	
}
