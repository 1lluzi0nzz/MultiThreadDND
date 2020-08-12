import java.util.*;

public class Merchant {
	
	public ArrayList<String> shop = new ArrayList<String>();
	public ArrayList<String> posNames = new ArrayList<String>();
	
    public Merchant(){
    	posNames.add("Ghorbash"); posNames.add("Ricky"); posNames.add("Chad");
    	Random r = new Random(3);
    	if(r.nextInt(posNames.size()) == 0) {
    		for(int i = 0; i < 4; i++) {
    			shop.add("Shield");
    		}
    	}else if(r.nextInt(posNames.size()) == 1) {
    		for(int i = 0; i < 4; i++) {
    			shop.add("Fireball");
    		}
    	}else if(r.nextInt(posNames.size()) == 2) {
    		for(int i = 0; i < 4; i++) {
    			shop.add("Potion");
    		}
    	}
    }
    
    public String getMerchantName(int i) {
    	if(i < posNames.size() && i >= 0) {
    		return posNames.get(i);
    	}else {
    		return "COULD NOT GET MERCHANTS NAME";
    	}
    }
}
