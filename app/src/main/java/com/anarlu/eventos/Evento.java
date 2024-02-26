package com.anarlu.eventos;

public class Evento {
    private String ID_evento;
    private String Nombre;
    private String Ubicacion;
    private String Descripicion;
    private String Hora_inicio;
    private String Hora_final;
    private String ID_usuario;
    private String Fecha_inicial;
    private String Fecha_final;
    private String Nombre_usuario;
    private String Tipo;

    // Constructor vacío requerido para Firestore
    public Evento() {}
    // Constructor con todos los campos
    public Evento(String ID_evento, String Nombre, String Ubicacion, String Descripicion, String Hora_inicio, String Hora_final,String Fecha_inicial,String Fecha_final,String Tipo,String Nombre_usuario, String ID_usuario) {
        this.ID_evento = ID_evento;
        this.Nombre = Nombre;
        this.Ubicacion = Ubicacion;
        this.Descripicion = Descripicion;
        this.Hora_inicio = Hora_inicio;
        this.Hora_final = Hora_final;
        this.ID_usuario = ID_usuario;
        this.Fecha_inicial=Fecha_inicial;
        this.Fecha_final=Fecha_final;
        //foto
        this.Nombre_usuario=Nombre_usuario;
        this.Tipo=Tipo;
    }

    // Getters y setters para cada campo

    public String getTipo() {return Tipo;}

    public void setTipo(String Tipo) {this.Tipo = Tipo;}

    public String getFecha_inicial() {return Fecha_inicial;}

    public void setFecha_inicial(String Fecha_inicial) {this.Fecha_inicial = Fecha_inicial;}

    public String getFecha_final() {return Fecha_final;}

    public void setFecha_final(String Fecha_final) {this.Fecha_final = Fecha_final;}

    public String getNombre_usuario() {return Nombre_usuario;}

    public void setNombre_usuario(String Nombre_usuario) {this.Nombre_usuario = Nombre_usuario;}

    public String getID_evento() {
        return ID_evento;
    }

    public void setID_evento(String ID_evento) {
        this.ID_evento = ID_evento;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public String getUbicacion() {
        return Ubicacion;
    }

    public void setUbicacion(String Ubicacion) {
        this.Ubicacion = Ubicacion;
    }

    public String getDescripicion() {
        return Descripicion;
    }

    public void setDescripicion(String Descripicion) {
        this.Descripicion = Descripicion;
    }

    public String getHora_inicio() {
        return Hora_inicio;
    }

    public void setHora_inicio(String Hora_inicio) {
        this.Hora_inicio = Hora_inicio;
    }

    public String getHora_final() {
        return Hora_final;
    }

    public void setHora_final(String Hora_final) {
        this.Hora_final = Hora_final;
    }

    public String getID_usuario() {
        return ID_usuario;
    }

    public void setID_usuario(String ID_usuario) {
        this.ID_usuario = ID_usuario;
    }
}
