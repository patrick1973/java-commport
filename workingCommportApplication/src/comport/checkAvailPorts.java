package comport;

import gnu.io.CommPortIdentifier;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 *
 * @author Patrick van ieperen
 */
public class checkAvailPorts
{
/*--------------------Data velden---------------------------------------------*/ 
     Enumeration port_list = CommPortIdentifier.getPortIdentifiers();
     private final ArrayList<String> listOfPorts;
     
/*--------------------Constructor---------------------------------------------*/
    public checkAvailPorts()
    {
       listOfPorts = new ArrayList<>();
       
       while( port_list.hasMoreElements())
       {
        CommPortIdentifier port_id = ( CommPortIdentifier) port_list.nextElement(); 
        if (port_id.getPortType() == CommPortIdentifier.PORT_SERIAL)
        {
            listOfPorts.add(port_id.getName());
        }
       }  
    }

/*--------------------Properties----------------------------------------------*/
    /**
     * 
     * @return arrayList with availible comm ports
     */
    public ArrayList<String> getListOfPorts()
    {
        return listOfPorts;
    }             
}
