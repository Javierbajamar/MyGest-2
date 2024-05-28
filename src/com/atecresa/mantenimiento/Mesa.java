package com.atecresa.mantenimiento;

import com.atecresa.clientes.Clientes;

import java.util.Objects;

public class Mesa {


    //LINEAS, MOVER, ELIMINAR, ETC

	// {"getdoc":{"CABECERA":
	/*
	 * {"ID":"60000739", "IDTPV":"12", "TIPO":"99", "IDCONTADOR":"1",
	 * "NUMERO":"24", "FECHA":"30\/03\/2015 13:00:51", "DTO":"0", "OBS":"-",
	 * "ESTADO":"1", "IGICINCLUIDO":"1", "IMPRESO":"0", "PREFACTURA":"0",
	 * "MESA":"3", "COMENSALES":"0", "IDVENDEDOR":"45000001", "TOTAL":"135",
	 * "CLIENTEPROVEEDOR"
	 * :{"ID":"1","CODIGO":"0000000000001","NOMBRE":"Ventas de contado"
	 * ,"EMPRESA"
	 * :"-","IGICINCLUIDO":"0","DTO":"0","IDNOMBRE_PRECIO":"1","IDFP":"1"
	 * ,"PIDTRAT":"1","OBS":"nueva obs"} }
	 */

	private String _id;
	private String _numero;
	private String _idvendedor;
	private String _estado;
	private String _fecha;
	private String _comensales;
	private String _total;
    private boolean _pagando=false;
	private boolean _bloqueada=false;

    //region COBROS
	private String _totalCobrado;
	private String _totalPendiente;

	public String getTotalCobrado() {
		return _totalCobrado;
	}

	public void setTotalCobrado(String _totalCobrado) {
		this._totalCobrado = _totalCobrado;
	}

	public String getTotalPendiente() {
		return _totalPendiente;
	}

	public void setTotalPendiente(String _totalPendiente) {
		this._totalPendiente = _totalPendiente;
	}

	//endregion


	// CLIENTE
	/*
	 * this._id = _id; this._nombre = _nombre; this._idNombrePrecio =
	 * _idNombrePrecio;
	 */

	private boolean clienteModificado = false;

	public boolean isClienteModificado() {
		return clienteModificado;
	}

	public void setClienteModificado(boolean mod){
		this.clienteModificado=mod;
	}


	private Clientes.Cliente _cliente;

	public void setCliente(Clientes.Cliente c) {
		if (this._cliente!=c){
			this._cliente = c;
			this.clienteModificado = true;
		}
		
	}

	public Clientes.Cliente getCliente() {
		return this._cliente;
	}
	
	public Mesa(){
		this._id = "-1";
		this._numero = "0";
		this._idvendedor = "-1";
		this._cliente=null;
		this._estado = "-1";
		this._fecha = "";
		this._comensales = "0";
		this._total = "0";
	}


	public String getId() {
		return _id;
	}

	public void setId(String _id) {
		this._id = _id;
	}

	public String getNumero() {
		return _numero;
	}

	public void setNumero(String _numero) {
		this._numero = _numero;
	}

	public void setIdvendedor(String _idvendedor) {
		this._idvendedor = _idvendedor;
	}

	public void setEstado(String _estado) {
		this._estado = _estado;
	}

	public void setFecha(String _fecha) {
		this._fecha = _fecha;
	}

	public String getComensales() {
		return _comensales;
	}

	
	public void setComensales(String _comensales) {
		if (!Objects.equals(_comensales, this._comensales)){
			this._comensales = _comensales;
            boolean _comensalesModificados = true;
		}
		
	}

	public String getTotal() {
		return _total;
	}

	public void setTotal(String _total) {
		this._total = _total;
	}

	public void setPrefactura(boolean _prefactura) {
        boolean _prefactura1 = _prefactura;
	}

	public boolean isPagando() {
		return _pagando;
	}

	public void setPagando(boolean _pagando) {
		this._pagando = _pagando;
	}

	public boolean isBloqueada() {
		return _bloqueada;
	}

	public void setBloqueada(boolean _bloqueada) {
		this._bloqueada = _bloqueada;
	}

}
