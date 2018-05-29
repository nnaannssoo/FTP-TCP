/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProyectoFTP;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
/**
 *
 * @author HP
 */
public class Cliente {
            private BufferedInputStream bis;
            private DataInputStream dis;
            private BufferedOutputStream bos;
            private DataInputStream nombre;
            private DataOutputStream dos;
          
          
            
            int len;
            Socket sock;
            
            byte[] byteArray;
            byte[] recibe;

            String archivoo;
            String archs ;
            public Cliente()
            {
            	try{
            		sock = new Socket ("localhost",20011);
            	}catch(IOException e){
            		System.out.println(e.getMessage());
            	}
               
            }
  
    public void recibir_archivo ()
    {
                try {
                    //ServerSocket servidor = new ServerSocket(20011);
                    sock = new Socket ("localhost",20011);
                    recibe = new byte[1024];
                    bis = new BufferedInputStream(sock.getInputStream());

                    nombre= new DataInputStream(sock.getInputStream());
                    String aux= nombre.readUTF();
                    archs="Recibido_"+aux;

                    //byte[] buffer = new byte[1024];                                                          
                    //File f1= new File(archivo1);
                    bos= new BufferedOutputStream(new FileOutputStream(archs));
                    
                    while((len = bis.read(recibe))!= -1)
                    {
                        bos.write(recibe,0,len);
                    }   
                    //bis.close();
                    
                    bos.close();
                    
                    System.out.println("Archivo "+aux+" descargado");
                   
                    //br.close();
                
                } catch (IOException ex) 
                {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void enviar_archivo() 
    {
                try {
                    //Socket cliente= new Socket("192.168.43.231",20011);
                    
                    
                    JFileChooser selectorArchivos = new JFileChooser();
                    selectorArchivos.showOpenDialog(selectorArchivos);
                    selectorArchivos.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    
                    File f = selectorArchivos.getSelectedFile();
                    
                    //El BufferedInputStream nos permite leer el archivo que queremos enviar
                    bis= new BufferedInputStream(new FileInputStream(f)); //Recibe como parametros un file input Stream
                    //Abrir el flujo de salida del clinete al servidor
                    bos = new BufferedOutputStream(sock.getOutputStream());
                    //Le vamos a enviar al servidor el nombre del archivo
                    dos=new DataOutputStream(sock.getOutputStream());
                    dos.writeUTF(f.getName());
                    //enviar datos del archivo
                    //Dividir el archivo para irlo enviando linea por linea
                    byteArray = new byte[1024]; //Sirve para indicar la cnatidad máxima de bytes que voy a leer
                    //Necesitamos decirle qué cantidad de datos estamos enviando para que
                    //el servidor sepa cuando va a terminar de recibir

                    while((len=bis.read(byteArray)) != -1)
                    {
                        bos.write(byteArray,0,len);
                    }   
                    
                    //bis.close();
                    bos.close();
                   
                } 
                catch (IOException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public void enviarMsj(String msj)
    {
        try {
            dos = new DataOutputStream(sock.getOutputStream());
            dos.writeUTF(msj);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    
    
    public void cerrar()
    {
                try {
                    bis.close();
                    bos.close();
                } catch (IOException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    public String leerMsj()
    {
        String msj = "";
        try {
            dis = new DataInputStream(sock.getInputStream());
            msj = dis.readUTF();
            return msj;
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());          
            return msj;
        }
    }
    
    public Object leerObjeto() {//Para leer el objeto que llega del cliente
        try {
            InputStream is = sock.getInputStream();
            ObjectInputStream msj = new ObjectInputStream(is);

            return msj.readObject();
            
        } catch (IOException ex) {
            return null;
        }       catch (ClassNotFoundException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
    }
   
    
    public static void main (String args [])
    {
        
        Cliente a=new Cliente();
        System.out.println("Menu");
        System.out.println("1) Ingresar");
        System.out.println("2) Registrarse");
        Scanner s = new Scanner(System.in);
        int entrada = s.nextInt();
        a.enviarMsj(String.valueOf(entrada));
        switch (entrada){
            case 1:
                System.out.println("Introduzca su nombre de usuario: ");
                Scanner us = new Scanner(System.in);
                String user = us.nextLine();
                System.out.println("Introduzca su contraseña");
                Scanner ps = new Scanner(System.in);
                String pass = ps.nextLine();
                String mandar = user+" "+pass;
                
                a.enviarMsj(mandar);
                
                if("Valido".equals(a.leerMsj())){
                    
                    double archivos=Double.parseDouble(a.leerMsj());
                    int cantidad= (int) archivos;
                    String []archivs= new String[cantidad];
                    System.out.println("Hola: " + user);
                    System.out.println("Tienes los siguientes archivos disponibles:");
                   
                    for(int y=1; y<=cantidad; y++)
                    {
                        String aux=a.leerMsj();
                        archivs[y-1]=aux;
                        System.out.println(y+") "+aux);
                    }
                    
                    System.out.println("");
                    System.out.println("¿Que deseas hacer?");
                    System.out.println("1) Descargar un archivo");
                    System.out.println("2) Subir un archivo");
                    Scanner op = new Scanner(System.in);
                    int opc=op.nextInt();
                    
                    switch (opc){
                        case 1:
                        {
                            a.enviarMsj("descarga");
                            System.out.println("¿Que archivo deseas bajar?");
                            Scanner j= new Scanner(System.in);
                            int o=j.nextInt();
                            
                            if(o< 1 || o>cantidad)
                            {
                                System.out.println("No existe ese archivo");
                            }
                            else
                            {
                                o--;
                                a.enviarMsj(String.valueOf(o));
                                a.recibir_archivo();
                            }
                            
                            break;
                        }
                        case 2:
                        {
                            a.enviarMsj("subir");
                            a.enviar_archivo();
                            break;
                        }
                        default:
                 
                    
                  
                 
                }
             }
                else
                {
                    
                    System.out.println("No fueron validos los datos de ingreso");
                }
                break;
            case 2:
                System.out.println("Introduzca su nombre de usuario: ");
                Scanner x = new Scanner(System.in);
                String nombre = x.nextLine();
                System.out.println("Introduzca su contraseña");
                Scanner y = new Scanner(System.in);
                String contra= y.nextLine();
                a.enviarMsj(nombre);
                a.enviarMsj(contra);
                //System.out.println(a.leerMsj());
                
                break;
            default:
                System.out.println("Opcion invalida");
                break;
        }
        
        
        
        
        //a.recibir_archivo();
            
                    
    }
}