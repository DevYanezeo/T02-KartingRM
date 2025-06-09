import React, { useState } from 'react';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import { ROUTES } from '../apiRoutes';
import { FaChartBar, FaCalendarAlt, FaSearch, FaSpinner, FaUsers, FaRoute } from 'react-icons/fa';
import './Reportes.css';
import { getApiBase } from '../getApiBase';

const Reportes = () => {
  const [desde, setDesde] = useState('2024-01-01T00:00:00');
  const [hasta, setHasta] = useState('2024-12-31T23:59:59');
  const [ingresosPorVueltas, setIngresosPorVueltas] = useState([]);
  const [ingresosPorPersonas, setIngresosPorPersonas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchReportes = async () => {
    setLoading(true);
    setError(null);
    try {
      const [vueltasRes, personasRes] = await Promise.all([
        axios.get(`${getApiBase()}${ROUTES.INCOME_BY_LAPS}?desde=${desde}&hasta=${hasta}`),
        axios.get(`${getApiBase()}${ROUTES.INCOME_BY_PEOPLE}?desde=${desde}&hasta=${hasta}`)
      ]);
      setIngresosPorVueltas(vueltasRes.data);
      setIngresosPorPersonas(personasRes.data);
    } catch (err) {
      setError('Error al cargar los reportes');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    fetchReportes();
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('es-CL', {
      style: 'currency',
      currency: 'CLP'
    }).format(value);
  };

  return (
    <div className="page-container">
      <Navbar />
      <main className="reportes-page">
        <div className="reportes-header">
          <h1>
            <FaChartBar /> Reportes de Ingresos
          </h1>
          <form onSubmit={handleSubmit} className="fecha-form">
            <div className="fecha-input-group">
              <label>
                <FaCalendarAlt /> Desde
              </label>
              <input 
                type="datetime-local" 
                value={desde.slice(0,16)} 
                onChange={e => setDesde(e.target.value+':00')} 
                required 
              />
            </div>
            <div className="fecha-input-group">
              <label>
                <FaCalendarAlt /> Hasta
              </label>
              <input 
                type="datetime-local" 
                value={hasta.slice(0,16)} 
                onChange={e => setHasta(e.target.value+':00')} 
                required 
              />
            </div>
            <button type="submit" className="consultar-btn" disabled={loading}>
              {loading ? <FaSpinner /> : <FaSearch />}
              {loading ? 'Consultando...' : 'Consultar'}
            </button>
          </form>
        </div>

        {error && <div className="error-message">{error}</div>}
        {loading && (
          <div className="loading-message">
            <FaSpinner /> Cargando reportes...
          </div>
        )}

        {/* Reporte 1: Ingresos por Vueltas */}
        <section className="reporte-section">
          <h2><FaRoute /> Ingresos por Vueltas</h2>
          {ingresosPorVueltas.length === 0 ? (
            <div className="no-data-message">No hay datos para el rango seleccionado.</div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="reporte-table">
                <thead>
                  <tr>
                    <th>Tipo</th>
                    {ingresosPorVueltas[0] && Object.keys(ingresosPorVueltas[0].meses).map(mes => (
                      <th key={mes}>{mes}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {ingresosPorVueltas.map((row, idx) => (
                    <tr key={idx}>
                      <td>{row.tipo}</td>
                      {Object.values(row.meses).map((valor, i) => (
                        <td key={i}>{formatCurrency(valor)}</td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        {/* Reporte 2: Ingresos por Personas */}
        <section className="reporte-section">
          <h2><FaUsers /> Ingresos por Personas</h2>
          {ingresosPorPersonas.length === 0 ? (
            <div className="no-data-message">No hay datos para el rango seleccionado.</div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table className="reporte-table">
                <thead>
                  <tr>
                    <th>Rango de Personas</th>
                    {ingresosPorPersonas[0] && Object.keys(ingresosPorPersonas[0].meses).map(mes => (
                      <th key={mes}>{mes}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {ingresosPorPersonas.map((row, idx) => (
                    <tr key={idx}>
                      <td>{row.rangoPersonas}</td>
                      {Object.values(row.meses).map((valor, i) => (
                        <td key={i}>{formatCurrency(valor)}</td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>
      <Footer />
    </div>
  );
};

export default Reportes;
