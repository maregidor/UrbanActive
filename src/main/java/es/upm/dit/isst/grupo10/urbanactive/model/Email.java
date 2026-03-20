package es.upm.dit.isst.grupo10.urbanactive.model;

public Class Email {

    private final String direccion;

    // Expresión regular para validar el formato
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    public Email (String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (!PATTERN.matcher(direccion).matches()) {
            throw new IllegalArgumentException("El formato del email es inválido");
        }
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(direccion, email.direccion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direccion);
    }

}