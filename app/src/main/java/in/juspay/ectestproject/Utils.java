package in.juspay.ectestproject;

public class Utils {

    public static String getApiKey(String mid){
        switch (mid){
            case "foodpanda" : return "B7171B010954EE1AB0D5D28FB00472";
            case "paypal" : return "9C3B8EF4C2C4822A5EF434C81214EC";
            case "dunzo" : return "6EAEEC04D15415594A433F2A2C7573";
            case "1mg" : return "3D1F3A21442240099A46D5E1D2664813";
            case "gaana" : return "F2BA9D741044FD08EAAA793C02395C";
            case "Gaana" : return "E7080E5194D4C89A045FABF81220C3";
            case "milkbasket" : return "BE62AB31454486180C7FC31219E9FE";
            case "dream11" : return "C9F62CB5C1384FD39D1BC55E76524529";
            case "railyatri_sandbox" : return "5AC4C22C87D41038EEFC56EC91A31D";
        }
        return "";

    }
}


