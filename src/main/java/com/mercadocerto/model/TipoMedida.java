package com.mercadocerto.model;

/**
 * Tipo de medida de uma embalagem de produto. Define qual semântica
 * os campos {@code quantidade}, {@code peso} e {@code unidade} assumem
 * em {@link Produto}.
 */
public enum TipoMedida {
    PESO,
    VOLUME,
    UNIDADE,
    PACK
}
