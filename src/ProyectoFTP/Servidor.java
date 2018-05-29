/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ProyectoFTP;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author HP
 */
public class Servidor {
     private ServerSocket servidor;
     private Socket cliente;
      ArrayList  usuarios;
     private OutputStream os;
     private DataInputStream dis;
     private DataOutputStream dos;
     private InputStream is;
     private ObjectInputStream entrada;
     private ObjectOutputStream salida;
     String[] arr_res;
     //private ObjectOutputStream salida;
    
     public Servidor (int puerto)
       {     
       try 
        {
             servidor= new ServerSocket(puerto); //El servidor esta a la escucha en este puerto
            //Crear el socket para el cliente; del servidor acepte la conexión del cliente 
              File dir = new File ("Archivos");
             dir.mkdir();
             cliente= servidor.accept();
            
            //Poner el los flujos de entrada y de salida 
             //os= cliente.getOutputStream(); //Se trae el flujo de salida del cliente
            // Esta clase me va a permitir transmitir infor mensajes, etc
             //is= cliente.getInputStream();
             //entrada= new ObjectInputStream(is);
            // salida= new ObjectOutputStream(os);
            
        } 
        catch (IOException ex) 
        {
            //Aqui vamos a mostrar el error que se genere
            System.out.println(ex.getMessage());
        }
       }
     public void cerrar_flujo()
     {
         try {
             cliente.close();
             is.close();
             //os.close();
             cliente.close();
         } catch (IOException ex) {
             System.out.println(ex.getMessage());
         }
            
     }
     public void recibir_archivo()
    {
        try {
        	//Primero se acepta la conexion con el cliente
        
                
        	//Buffer de la cantidad de bytes que seran igual en el cliente
        	byte buffer[] = new byte[1024];
                DataInputStream nombre= new DataInputStream(cliente.getInputStream());
                BufferedInputStream bis = new BufferedInputStream(cliente.getInputStream());
                File dir = new File ("Archivos");
                
                //File f= new File(archivo);//****
                String archivo = nombre.readUTF();
                BufferedOutputStream bos= new BufferedOutputStream(new FileOutputStream(dir+"/"+archivo));
                int len=0;
                while((len = bis.read(buffer))!= -1)
                {
                    bos.write(buffer,0,len);
                }
           
                
            bos.close();
            //bis.close(); 

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
     public void leer()
     {
          try 
        {       dis= new DataInputStream(cliente.getInputStream());
                //Se muestra por pantalla el mensaje recibido
                System.out.println(dis.readUTF()); 
        }
         catch (IOException ex) 
        {
            //Aqui vamos a mostrar el error que se genere
            System.out.println(ex.getMessage());
        }
     }
    public Object recibir_objeto()
    {
         try {  
             return entrada.readObject();
           
         } catch (IOException ex) 
         {
             System.out.println(ex.getMessage());
             return null;
         } catch (ClassNotFoundException ex) 
         {
             System.out.println(ex.getMessage());
             return null;
         }
    }
    public String [] enviarLista_archivos()
    {
        
        String path = "C://Users/NancyB/Documents/Sexto Semestre/Aplicaciones para comunicaciones en Red/TCP/Archivos";
        
        arr_res = null;
        
        int size = 0;

        File f = new File( path );

        if ( f.isDirectory( )) {
            List<String> res   = new ArrayList<>();
            File[] arr_content = f.listFiles();
            size = arr_content.length;
            
            for(int i = 0;i<size;i++){
                if(arr_content[i].isFile()){
                    res.add(arr_content[i].getName());
                }
            }
            arr_res = res.toArray(new String[0]);
        }
            this.enviarMsj(String.valueOf(size));
        for(int j=0; j<size;j++){
             this.enviarMsj(arr_res[j]);
        }
        return arr_res;
    }
        

    
    public void registrar(String usuario, String pass)
    {
     String concat= usuario+" "+pass;
     
         try {
             cargar_usuarios();
         } catch (IOException ex) {
             Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
         }
     
     
     if(validar(concat))
     {
         System.out.println("Ya existe ese usuario");
         this.enviarMsj("Ya hay un usuario con ese nombre");
     }
     else
     {
         System.out.println("Registrando ...");
       String path = "C://Users/NancyB/Documents/Sexto Semestre/Aplicaciones para comunicaciones en Red/TCP/usuarios.txt";
         try(FileWriter fw=new FileWriter(path,true);
            FileReader fr=new FileReader(path)){
            //Escribimos en el fichero
            fw.append("\r\n"+concat);
 
            //Guardamos los cambios del fichero
            fw.flush();
            this.enviarMsj("Registro exitoso");
     }   catch (IOException ex) {
             Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
         }
     }
    }
        public String leerMsj()
    {
       String msj=""; 
        try {
            dis= new DataInputStream(cliente.getInputStream());
             
                 msj= dis.readUTF();
                
             
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return msj;
    }
    public  void cargar_usuarios() throws FileNotFoundException, IOException{
            
  

                usuarios = new ArrayList();
                String archivo="";
               
                File abrirArch= new File ("usuarios.txt");
                
                if (abrirArch != null) 
                {
                    FileReader fr = new FileReader(abrirArch);
                         BufferedReader br = new BufferedReader(fr);
 
                            String linea="";
                            while((linea = br.readLine()) != null)
                            {
                               //String a []= new String [2];
                               //a=linea.split(" ");
                               usuarios.add(linea);
                            }
                              
 
                    fr.close();
                    System.out.println("");                   
                    }else
            
             {
                System.out.println("No se pudo leer el archivo...");
             } 
    }
    public boolean validar(String userpass)
    {
        

            if(usuarios.contains(userpass))
            {
                return true;
            }
            
            else
            {
                return false;
            }
        
    }
      public void enviarMsj(String msj)
    {
        try {
            dos = new DataOutputStream(cliente.getOutputStream());
            dos.writeUTF(msj);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
       public void enviar_objeto(Object o)
      {
          try {
            os = cliente.getOutputStream();
            salida = new ObjectOutputStream(os);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
          
      
      }
       public void enviar_archivo(String name) 
    {
                try {
                    
                    cliente = servidor.accept();
                    String path = "C://Users/NancyB/Documents/Sexto Semestre/Aplicaciones para comunicaciones en Red/TCP/Archivos"+"/"+name;
                    File f1= new File(path);

                    //El BufferedInputStream nos permite leer el archivo que queremos enviar
                    BufferedInputStream bis= new BufferedInputStream(new FileInputStream(f1)); //Recibe como parametros un file input Stream
                        //Abrir el flujo de salida 
                    BufferedOutputStream bos = new BufferedOutputStream(cliente.getOutputStream());

                    dos=new DataOutputStream(cliente.getOutputStream());
                    dos.writeUTF(f1.getName());

                    //Dividir el archivo para irlo enviando linea por linea
                    
                    byte[] byteArray = new byte[1024];

                    int len=0;
                    while((len=bis.read(byteArray)) != -1)
                    {
                        bos.write(byteArray,0,len);
                    }  
                    System.out.println("Se ha enviado el archivo satisfactoriamente");
                    bos.close();
                    //this.cerrarflujos();
                    
                } 
                catch (IOException ex) {
                    Logger.getLogger(ClienteArchivos.class.getName()).log(Level.SEVERE, null, ex);
                }
    }

    public String[] getarchivos() {
        return arr_res;
    }
       
       
    public static void main(String args[]) throws InterruptedException 
    {
         try {
             Servidor S= new Servidor(20011);
             S.cargar_usuarios(); 
              double archivos=Double.parseDouble(S.leerMsj());
                    int opcion= (int) archivos;
                    
                    switch(opcion)
                    {
                        case 1:
                        {
                            //Aqui ingreso un usuario y le va a mostrar los archivods
                            if(S.validar(S.leerMsj()))
                            {
                                System.out.println("Usuario Valido");
                                S.enviarMsj("Valido");
                                S.enviarLista_archivos();
                                String hacer=S.leerMsj();
                                
                                
                                if(hacer.equals("subir"))
                                {
                                    S.recibir_archivo();
                                    
                                }else if(hacer.equals("descarga"))
                                {
                                    
                                    double aux=Double.parseDouble(S.leerMsj()); 
                                    int indice= (int)aux;
                                    
                                    System.out.println("Enviando archivo"+ S.getarchivos()[indice]);
                                    S.enviar_archivo(S.getarchivos()[indice]);
                                    
                                    
                                    
                                }
                                
                            }
                           else
                            {
                                System.out.println("Usuario no valido");
                            }
                            break;
                        } 
                        case 2:
                        {
                            System.out.println("Se va a registrar, esperando informacion del cliente ");
                            
                            String a=S.leerMsj();
                            String b=S.leerMsj();
                            S.registrar(a, b);
                            
                            break;
                        }
                           
                    }
             
         } catch (IOException ex) {
             Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    
}
