package trade.bean;

import com.caiyi.lottery.tradesystem.util.StringUtil;

import java.util.*;

public class FilterResult {

    private HashMap<String, String> items = new HashMap<>();
    private HashMap<String, String> gglists = new HashMap<>();

    private List<String> list = new ArrayList<>();
    private String currentCode;
    private int totalMoney;

    public void addCode(String code){
        setCurrentCode(code);
        list.add(code);
    }

    public String getAllCode(){
        StringBuffer sb = new StringBuffer();
        for(String s:list){
            sb.append(s);
            sb.append(";");
        }
        return sb.toString();
    }

    public String getAllCodeToFile(){
        StringBuffer sb = new StringBuffer();
        for(String s:list){
            sb.append(s);
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void addMoney(int currentMoney) {
        this.totalMoney += currentMoney;
    }

    public String getCurrentCode() {
        return currentCode;
    }

    public void setCurrentCode(String currentCode) {
        this.currentCode = currentCode;
    }

    public void putItem(String teamItem) {
        items.put(teamItem, teamItem);
    }

    public void putItems(HashMap<String, String> teamItems){
        Iterator<String> keys = teamItems.keySet().iterator();
        while(keys.hasNext()){
            String key = keys.next();
            items.put(key, key);
        }
    }

    public String getTeamItems() {
        Iterator<String> keys = items.keySet().iterator();
        List<String> list = new ArrayList<String>();
        while (keys.hasNext()) {
            list.add(keys.next());
        }
        Collections.sort(list);
        int size = list.size();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(list.get(i));
            if(i != size - 1){
                sb.append(",");
            }
        }
        list.clear();
        return sb.toString();
    }

    public String getTeamItems1() {
        Iterator<String> keys = items.keySet().iterator();
        List<Integer> list = new ArrayList<Integer>();
        while (keys.hasNext()) {
            list.add(StringUtil.getNullInt(keys.next()));
        }
        Collections.sort(list);
        int size = list.size();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(list.get(i));
            if(i != size - 1){
                sb.append(",");
            }
        }
        list.clear();
        return sb.toString();
    }

    public void putGglist(String gg) {
        String[] a = gg.split(",");
        for (int i=0;i<a.length;i++){
            gglists.put(a[i], a[i]);
        }
    }

    public void putGglists(HashMap<String, String> ggs){
        Iterator<String> keys = ggs.keySet().iterator();
        while(keys.hasNext()){
            String key = keys.next();
            gglists.put(key, key);
        }
    }

    public String getGglists() {
        Iterator<String> keys = gglists.keySet().iterator();
        List<String> list = new ArrayList<String>();
        while (keys.hasNext()) {
            list.add(keys.next());
        }
        Collections.sort(list);
        int size = list.size();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(list.get(i));
            if(i != size - 1){
                sb.append(",");
            }
        }
        list.clear();
        return sb.toString();
    }
}
