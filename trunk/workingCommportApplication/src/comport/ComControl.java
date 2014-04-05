package comport;

//import javax.comm.*;
import java.util.*;
import gnu.io.*;

/** List all the ports available on the local machine. **/
public class ComControl {
  public static void main (String [] args) {

    Enumeration port_list = CommPortIdentifier.getPortIdentifiers ();

    while (port_list.hasMoreElements()) { 
      CommPortIdentifier port_id =(CommPortIdentifier) port_list.nextElement ();

      if (port_id.getPortType () == CommPortIdentifier.PORT_SERIAL) {
          System.out.println ("Serial port: " + port_id.getName());
      } 
      else if (port_id.getPortType () ==
               CommPortIdentifier.PORT_PARALLEL) {
         System.out.println ("Parallel port: " + port_id.getName ());
      }
      else
        System.out.println ("Other port: " + port_id.getName ());
    }
  } // main
} // class PortList

