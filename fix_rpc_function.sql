-- Script para corregir la función RPC get_assignments_with_details
-- El problema es que algunas columnas retornan varchar(150) en lugar de text

-- Eliminar función existente
DROP FUNCTION IF EXISTS public.get_assignments_with_details(text, text);

-- Crear función corregida con CAST explícito a TEXT
CREATE OR REPLACE FUNCTION public.get_assignments_with_details(
    p_filter_type text DEFAULT 'vencidas',
    p_search_term text DEFAULT NULL
)
RETURNS TABLE (
    id_asignacion integer,
    id_miembro integer,
    platform text,
    nombre_completo text,
    id_producto_digital integer,
    nombre_producto text,
    fecha_inicio date,
    fecha_fin date,
    activa boolean,
    cancelada boolean,
    usos_disponibles integer,
    usos_total integer
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        v.id_asignacion,
        v.id_miembro,
        v.platform::text,
        v.nombre_completo::text,
        v.id_producto_digital,
        v.nombre_producto::text,
        v.fecha_inicio,
        v.fecha_fin,
        v.activa,
        v.cancelada,
        v.usos_disponibles,
        v.usos_total
    FROM public.v_assignments_details v
    WHERE 
        CASE 
            WHEN p_filter_type = 'vencidas' THEN v.fecha_fin < CURRENT_DATE
            WHEN p_filter_type = 'por_vencer' THEN v.fecha_fin >= CURRENT_DATE AND v.fecha_fin <= CURRENT_DATE + INTERVAL '7 days'
            WHEN p_filter_type = 'activas' THEN v.activa = true AND v.cancelada = false
            ELSE true
        END
        AND (
            p_search_term IS NULL 
            OR v.nombre_completo ILIKE '%' || p_search_term || '%'
            OR v.id_miembro::text ILIKE '%' || p_search_term || '%'
        )
    ORDER BY v.fecha_fin DESC;
END;
$$ LANGUAGE plpgsql;

-- Dar permisos
GRANT EXECUTE ON FUNCTION public.get_assignments_with_details TO authenticated;
