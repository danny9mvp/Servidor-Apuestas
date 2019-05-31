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
    private static DataOutputStream dos=null; 
    private static int pozoAcumulado=0;
    
    public static void enviarManoInicial(String mano){                                     
        try {            
            dos = new DataOutputStream(clienteSocket.getOutputStream());
            dos.flush();
            dos.writeUTF("mano,"+mano);             
            logger.log(Level.INFO, "Mano {0} enviada al jugador.", mano);
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
    
    public static void enviarCarta(String carta){                             
        try {                        
            dos.flush();
            dos.writeUTF("carta,"+carta);             
            logger.log(Level.INFO, "Carta {0} enviada al jugador.", carta);
        } catch (IOException ex) {
            ex.printStackTrace();
        }       
    }
    
    public static void jugadorGana(String manoGanadora, String manoCasa){        
        try {                        
            dos.flush();
            dos.writeUTF("ganaste,"+","+manoGanadora+","+manoCasa);             
            logger.log(Level.INFO, "El jugador ha ganado {0}.", pozoAcumulado);
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }
    
    public static void jugadorPierde(String manoPerdedora, String manoCasa){        
        try {                        
            dos.flush();
            dos.writeUTF("perdiste,"+","+manoPerdedora+","+manoCasa);             
            logger.log(Level.INFO, "El jugador ha perdido {0}.", pozoAcumulado);
        } catch (IOException ex) {
            ex.printStackTrace();
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
                            String manoJugador="";
                            while(true){
                                entrada = dis.readUTF();                                
                                switch(entrada.split(",")[0]){
                                    case "iniciar":
                                        blackJack = new BlackJack();
                                        manoCasa = blackJack.repartirMano(blackJack.generarIndiceAleatorio(),blackJack.generarIndiceAleatorio());
                                        manoJugador = blackJack.repartirMano(blackJack.generarIndiceAleatorio(),blackJack.generarIndiceAleatorio());
                                        logger.info("Cartas jugador: "+(manoJugador));
                                        logger.log(Level.INFO, "La mano de la casa es {0}",manoCasa);
                                        apuestaJugador = Integer.parseInt(entrada.split(",")[1]);
                                        logger.log(Level.INFO, "El jugador ha apostado {0}",apuestaJugador);
                                        pozoAcumulado += 2*apuestaJugador;
                                        enviarManoInicial(manoJugador);
                                        break;
                                    case "pedir":
                                        int indiceCartaJugador=blackJack.generarIndiceAleatorio();
                                        String carta = blackJack.pedirCarta(indiceCartaJugador);                                                                                                                                                               
                                        enviarCarta(carta);
                                        manoJugador+="|"+carta;                                        
                                        logger.log(Level.INFO,"El jugador ha pedido una carta a su mano {0}", manoJugador);                                        
                                        break;
                                    case "quedarse":                                                                             
                                        if(blackJack.calcularValorMano(manoJugador) > blackJack.calcularValorMano(manoCasa) || 
                                                blackJack.calcularValorMano(manoCasa) > 21){
                                            jugadorGana = true;
                                            logger.info("El jugador gana.");
                                            jugadorGana(manoJugador, manoCasa);
                                        }
                                        else if(blackJack.calcularValorMano(entrada.split(",")[1]) < 
                                            blackJack.calcularValorMano(manoCasa)){
                                            String manoPerdedora=entrada.split(",")[1];
                                            logger.info("El jugador pierde.");
                                            jugadorPierde(manoPerdedora, manoCasa);
                                        }
                                        break;                                    
                                }
                                logger.info("Mano jugador: "+blackJack.calcularValorMano(manoJugador));
                                if(blackJack.calcularValorMano(manoJugador) > 21){
                                    String manoPerdedora=entrada.split(",")[1];
                                    logger.log(Level.INFO, "El jugador ha perdido.");
                                    jugadorPierde(manoPerdedora, manoCasa);                                
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
