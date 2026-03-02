-- Crear vista para asignaciones con detalles completos
CREATE OR REPLACE VIEW public.v_assignments_details AS
SELECT 
    aa.id_asignacion,
    aa.id_miembro,
    aa.id_producto_digital,
    aa.fecha_inicio,
    aa.fecha_fin,
    aa.activa,
    aa.cancelada,
    aa.fecha_cancelacion,
    aa.fecha_registro,
    aa.usos_disponibles,
    aa.usos_total,
    m.nombres || ' ' || m.apellido_paterno || ' ' || m.apellido_materno as nombre_completo,
    pd.nombre as nombre_producto,
    COALESCE(dt.platform, 'N/A') as platform
FROM asignaciones_activas aa
LEFT JOIN miembros m ON aa.id_miembro = m.id_miembro
LEFT JOIN ca_productos_digitales pd ON aa.id_producto_digital = pd.id_producto_digital
LEFT JOIN (
    SELECT DISTINCT ON (id_miembro) id_miembro, platform 
    FROM device_tokens 
    ORDER BY id_miembro, updated_at DESC
) dt ON aa.id_miembro = dt.id_miembro;

-- Función RPC para consultas dinámicas
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
        v.platform,
        v.nombre_completo,
        v.id_producto_digital,
        v.nombre_producto,
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

-- Dar permisos de ejecución
GRANT EXECUTE ON FUNCTION public.get_assignments_with_details TO authenticated;
GRANT SELECT ON public.v_assignments_details TO authenticated;
