package com.anarlu.eventos;

public class Evento {
    private String ID_evento;
    private String Nombre;
    private String Ubicacion;
    private String descripcion;
    private String Hora_inicio;
    private String Hora_final;
    private String ID_usuario;
    private String fecha_inicio;
    private String fecha_fin;
    private String name_user;

    // Constructor vacío requerido para Firestore
    public Evento() {}
    // Constructor con todos los campos
    public Evento(String ID_evento, String Nombre, String Ubicacion, String descripcion, String Hora_inicio, String Hora_final,String fecha_inicio,String fecha_fin,String name_user, String ID_usuario) {
        this.ID_evento = ID_evento;
        this.Nombre = Nombre;
        this.Ubicacion = Ubicacion;
        this.descripcion = descripcion;
        this.Hora_inicio = Hora_inicio;
        this.Hora_final = Hora_final;
        this.ID_usuario = ID_usuario;
        this.fecha_inicio=fecha_inicio;
        this.fecha_fin=fecha_fin;
        //foto
        this.name_user=name_user;
    }

    // Getters y setters para cada campo

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(String fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
