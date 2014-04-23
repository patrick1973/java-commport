package comport;


import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;



public class CommPort extends Thread implements SerialPortEventListener 
{    
/*--------------------Data velden---------------------------------------------*/ 
     Enumeration port_list = CommPortIdentifier.getPortIdentifiers();
     SerialPort serialPort;
     CommPortIdentifier port_id;
     private final ArrayList<String> listOfPorts;
     private static final int TIME_OUT = 2000;
     private int baudRate = 9600;
     private String selectedCommport;
     private BufferedReader input;
     private OutputStream output;
     private String receivedData;
     
     private boolean fetchingData = false;
     Thread readCycle;
     int teller=0;
     
     
     
/*--------------------Constructor---------------------------------------------*/
    public CommPort()
    {
        listOfPorts = new ArrayList<>();
        
        checkAvailiblePorts();
        
      
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
    
    public int getBaudRate()
    {
       return baudRate;
    }

    public void setBaudRate(int baudRate)
    {
        this.baudRate = baudRate;
    }
    
    public String getSelectedCommport()
    {
        return selectedCommport;
    }

    public void setSelectedCommport(String selectedCommport)
    {
        this.selectedCommport = selectedCommport;
    }
    
    public String getReceivedData()
    {
        return receivedData;
    }
    
/*--------------------Methodes------------------------------------------------*/     
 private void checkAvailiblePorts()
{
    while (port_list.hasMoreElements())
    {
        port_id = ( CommPortIdentifier) port_list.nextElement(); 
        if (port_id.getPortType() == CommPortIdentifier.PORT_SERIAL)
        {
            listOfPorts.add(port_id.getName());
        }
       }  
    } 
 
 public boolean openCommPort() throws Exception
 {
     try
     {
         
      
       // open serial port, and use class name for the appName.
       port_id = CommPortIdentifier.getPortIdentifier(selectedCommport); // laat het object naar de juiste (geselecteerde poort) kijken.
       serialPort = (SerialPort)  port_id.open(this.getClass().getName(),TIME_OUT);
      
       
       // set port parameters
       serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);       
       // open the streams
        
     }
     catch ( PortInUseException | UnsupportedCommOperationException ex)
     {
         return false;
     } 
     return true;
 }
 
 private void getDataFromPort()
 {
     try
     {
        input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        output = serialPort.getOutputStream();
        serialPort.addEventListener(this);
        serialPort.notifyOnDataAvailable(true);
     }   
     catch (IOException | TooManyListenersException ex)
     {
         Logger.getLogger(CommPort.class.getName()).log(Level.SEVERE, null, ex);
     }
             
 }
 /**
 * This should be called when you stop using the port.
 * This will prevent port locking on platforms like Linux.
 * @return 
 */ 
public synchronized boolean closeCommPort()
 {
     if (serialPort !=null)
     {
         serialPort.removeEventListener();
         serialPort.close();
         return true;
     }
     else
     {
         return false;
     }
 }
public void readData()
{
    getDataFromPort();
    try
    {
    if (!fetchingData)
    {
        readCycle = new Thread(this);
        readCycle.start();
    }
    fetchingData = true; 
    }
    catch ( IllegalThreadStateException ex)
        {
           JOptionPane.showMessageDialog(null,"Er is iets mis gegaan met het starten van de Thread","ERROR",JOptionPane.ERROR_MESSAGE);
        }
}

    @Override
    public synchronized void serialEvent(SerialPortEvent spe)
    {
        if (spe.getEventType() == SerialPortEvent.DATA_AVAILABLE) 
        {
            try
            {
                
                receivedData = input.readLine();
                
            }
            catch(IOException ex)
            {
               System.err.println(ex.toString());
            }
        }
        
    }
    
    @Override
    public void run() {
        try
            {
                while (!Thread.currentThread().isInterrupted() && fetchingData)
               { 
                   teller++;
                   CommPort.sleep(1000); 
                }
            }
            catch(InterruptedException ex)
            {
                JOptionPane.showMessageDialog(null,"Er is iets mis gegaan met Threads : " + ex.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
            }
            
    }

    

    

   
}
 
