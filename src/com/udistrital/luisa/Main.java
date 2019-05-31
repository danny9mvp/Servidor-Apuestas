/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udistrital.luisa;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Estudiante
 */
public class Main {
    public static final Logger logger = Logger.getLogger("LoggerServidor");
    private static ServerSocket serverSocket=null;
    private static Socket clienteSocket=null;        
    private static int pozoAcumulado=0;
    
    public static void enviarManoInicial(BlackJack blackJack){
        DataOutputStream dos=null;        
        int indiceCartaJugador1=blackJack.generarIndiceAleatorio();int indiceCartaJugador2=blackJack.generarIndiceAleatorio();
        
        String manoJugador = blackJack.repartirMano(indiceCartaJugador1, indiceCartaJugador2);
        try {            
            dos = new DataOutputStream(clienteSocket.getOutputStream());
            dos.flush();
            dos.writeUTF(manoJugador);             
            logger.log(Level.INFO, "Mano {0} enviada al jugador.", manoJugador);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        finally{
            if(dos != null)
                try {
                    dos.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void enviarCarta(BlackJack blackJack){
        DataOutputStream dos=null;              
        int indiceCartaJugador=blackJack.generarIndiceAleatorio();
        String carta = blackJack.pedirCarta(indiceCartaJugador);
        try {            
            dos = new DataOutputStream(clienteSocket.getOutputStream());
            dos.flush();
            dos.writeUTF(carta);             
            logger.log(Level.INFO, "Carta {0} enviada al jugador.", carta);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        finally{
            if(dos != null)
                try {
                    dos.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void jugadorGana(){
        DataOutputStream dos=null;
        try {            
            dos = new DataOutputStream(clienteSocket.getOutputStream());
            dos.flush();
            dos.writeUTF("ganaste,"+pozoAcumulado);             
            logger.log(Level.INFO, "El jugador ha ganado {0}.", pozoAcumulado);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        finally{
            if(dos != null)
                try {
                    dos.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void jugadorPierde(){
        DataOutputStream dos=null;
        try {            
            dos = new DataOutputStream(clienteSocket.getOutputStream());
            dos.flush();
            dos.writeUTF("perdiste,"+pozoAcumulado);             
            logger.log(Level.INFO, "El jugador ha perdido {0}.", pozoAcumulado);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        finally{
            if(dos != null)
                try {
                    dos.close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args){        
        try {
            serverSocket = new ServerSocket(9000);            
            try {
                logger.info("Eperando a que el jugador se conecte...");
                clienteSocket = serverSocket.accept();
                logger.info("Un jugador se ha conectado.");
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
            Thread hiloEscucha = new Thread(new Runnable() {
                public void run() {
                    String entrada = "";
                    DataInputStream dis = null;                      
                        try {
                            dis = new DataInputStream(clienteSocket.getInputStream());
                            BlackJack blackJack = null;
                            boolean jugadorGana=false;
                            int apuestaJugador = 0;
                            String manoCasa="";
                            while(true){
                                entrada = dis.readUTF();                                
                                switch(entrada.split(",")[0]){
                                    case "iniciar":
                                        blackJack = new BlackJack();
                                        manoCasa = blackJack.repartirMano(blackJack.generarIndiceAleatorio(),blackJack.generarIndiceAleatorio());
                                        logger.log(Level.INFO, "La mano de la casa es {0}",manoCasa);
                                        apuestaJugador = Integer.parseInt(entrada.split(",")[1]);
                                        pozoAcumulado += 2*apuestaJugador;
                                        enviarManoInicial(blackJack);
                                        break;
                                    case "pedir":
                                        enviarCarta(blackJack);
                                        logger.log(Level.INFO,"El jugador ha pedido una carta a su mano {0}",entrada.split(",")[1]);
                                        break;
                                    case "quedarse":
                                        String manoJugador = entrada.split(",")[1];                                        
                                        if(blackJack.calcularValorMano(manoJugador) > blackJack.calcularValorMano(manoCasa)){
                                            jugadorGana = true;
                                        }
                                        else if(blackJack.calcularValorMano(entrada.split(",")[1]) < 
                                            blackJack.calcularValorMano(manoCasa))
                                            jugadorPierde();
                                        break;
                                    case "reiniciar":                                        
                                        apuestaJugador = Integer.parseInt(entrada.split(",")[1]);
                                        pozoAcumulado += 2*apuestaJugador;
                                        enviarManoInicial(blackJack);
                                        break;
                                }
                                if(!jugadorGana){
                                    if(blackJack.calcularValorMano(entrada.split(",")[1]) > 21)
                                        jugadorPierde();                                
                                }
                            }
                        } catch (IOException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        } 
                    
                }
            });
            hiloEscucha.start();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
