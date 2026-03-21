package es.upm.dit.isst.grupo10.urbanactive.model;

Public class Nivel {

    private final double valor; 

    public Nivel(double valor) {
        if (valor < 0.0 || valor > 10.0) {
            throw new IllegalArgumentException("El valor del nivel debe estar entre 0.0 y 10.0");
        }
        this.valor = valor;
    }

    public Double getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NivelRequerido that = (NivelRequerido) o;
        return Double.compare(that.valor, valor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

}