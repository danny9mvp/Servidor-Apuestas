/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.udistrital.luisa;

/**
 *
 * @author Estudiante
 */
public class BlackJack {
    private String[] cartas = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
    
    public int generarIndiceAleatorio(){
        return (int) (Math.random() * 10);
    }
    
    public String repartirMano(int i, int j){        
        String mano = cartas[i]+cartas[j];
        return mano;
    }
    
    public String pedirCarta(int i){        
        return cartas[i];
    }
    
    public int calcularValorMano(String mano){
        int valor=0;
        for(int i=0;i<mano.length();i++){
            switch (mano.charAt(i)){
                case 'A':
                    valor+=1;
                    break;
                case '2':
                    valor+=2;
                    break;
                case '3':
                    valor+=3;
                    break;
                case '4':
                    valor+=4;
                    break;
                case '5':
                    valor+=5;
                    break;
                case '6':
                    valor+=6;
                    break;
                case '7':
                    valor+=7;
                    break;
                case '8':
                    valor+=1;
                    break;
                case '9':
                    valor+=1;
                    break;
                default:
                    valor+=10;
                    break;
            }
        }
        return valor;
    }    
}
