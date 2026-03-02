// Script JavaScript para consultar asignaciones de miembro ID 7
// Ejecutar con: node script.js
// Configurar variables de entorno antes de ejecutar:
//   export SUPABASE_URL="https://tu-proyecto.supabase.co"
//   export SUPABASE_ANON_KEY="tu-api-key"

const API_URL = process.env.SUPABASE_URL || 'https://ufnmqxyvrfionysjeiko.supabase.co/rest/v1';
const API_KEY = process.env.SUPABASE_ANON_KEY || 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVmbm1xeHl2cmZpb255c2plaWtvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTkyOTM0NDMsImV4cCI6MjA3NDg2OTQ0M30.pevhyNLyUXaxMHrk8LdwqOzojRbRISUaDxK3Xp-jt2o';

async function consultarAsignaciones() {
    console.log('🔍 CONSULTANDO ASIGNACIONES PARA MIEMBRO ID 7...');
    console.log('==================================================');

    try {
        const response = await fetch(`${API_URL}?select=*&limit=100`, {
            method: 'GET',
            headers: {
                'apikey': API_KEY,
                'Authorization': `Bearer ${API_KEY}`,
                'User-Agent': 'HTF-System-JS-Script/1.0',
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();

        console.log('✅ CONEXIÓN EXITOSA');
        console.log(`📊 STATUS CODE: ${response.status}`);
        console.log(`📋 TOTAL DE REGISTROS: ${data.length}`);
        console.log('');

        // Filtrar por ID 7
        const asignacionesId7 = data.filter(item => item.id_miembro === 7);

        if (asignacionesId7.length > 0) {
            console.log('🎯 ASIGNACIONES ENCONTRADAS PARA MIEMBRO ID 7:');
            console.log(`📊 CANTIDAD: ${asignacionesId7.length}`);
            console.log('');

            asignacionesId7.forEach((asignacion, index) => {
                console.log(`🏷️  ASIGNACIÓN ${index + 1}:`);
                console.log(`   ID ASIGNACIÓN: ${asignacion.id_asignacion}`);
                console.log(`   MIEMBRO ID: ${asignacion.id_miembro}`);
                console.log(`   PRODUCTO ID: ${asignacion.id_producto_digital}`);
                console.log(`   FECHA INICIO: ${asignacion.fecha_inicio}`);
                console.log(`   FECHA FIN: ${asignacion.fecha_fin}`);
                console.log(`   ACTIVA: ${asignacion.activa}`);
                console.log('   ----------------------------------------');
            });
        } else {
            console.log('❌ NO SE ENCONTRARON ASIGNACIONES PARA MIEMBRO ID 7');
        }

    } catch (error) {
        console.log('❌ ERROR EN LA CONSULTA:');
        console.log(error.message);
    }
}

// Ejecutar la consulta
consultarAsignaciones();
