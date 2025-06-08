import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import { FaFileInvoice, FaSearch, FaFileDownload, FaSync, FaCalendarAlt } from 'react-icons/fa';
import './Invoice.css';
import { ROUTES, getInvoiceDownloadUrl } from '../apiRoutes';

const API_BASE = window.API_URL || 'http://localhost:8080';
const Invoices = () => {
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterDate, setFilterDate] = useState('');

  // Función para cargar todas las boletas
  const fetchInvoices = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(`${API_BASE}${ROUTES.INVOICES}`);
      if (!response.data) throw new Error("No se recibieron datos");
      // Validar que la respuesta sea un array
      const invoicesData = Array.isArray(response.data) ? response.data : [];
      setInvoices(invoicesData);
    } catch (err) {
      setError("Error al cargar las boletas: " + (err.response?.data?.message || err.message));
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // Filtrar boletas por término de búsqueda y fecha
  const filteredInvoices = invoices.filter(invoice => {
    const matchesSearch = invoice.invoiceCode?.toLowerCase().includes(searchTerm.toLowerCase()) || 
                         invoice.nombreResponsable?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesDate = !filterDate || 
                       (invoice.fechaEmision && new Date(invoice.fechaEmision).toISOString().split('T')[0] === filterDate);
    return matchesSearch && matchesDate;
  });

  // Descargar boleta PDF
  const downloadInvoice = async (id) => {
    console.log('Intentando descargar boleta con id:', id); // Depuración
    if (!id) {
      setError('No se puede descargar la boleta: ID inválido.');
      return;
    }
    try {
      // Usar función utilitaria para construir la URL de descarga
      const downloadUrl = getInvoiceDownloadUrl(id);
      const response = await axios.get(downloadUrl, {
        responseType: 'blob'
      });
      // Crear URL para el blob
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `comprobante_${id}.pdf`);
      document.body.appendChild(link);
      link.click();
      // Limpiar
      link.parentNode.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (err) {
      setError('Error al descargar la boleta: ' + (err.response?.data?.message || err.message));
      console.error(err);
    }
  };

  useEffect(() => {
    fetchInvoices();
  }, []);

  return (
    <div className="invoices-page">
      <Navbar />
      <main className="invoices-container">
        <header className="invoices-header">
          <h1><FaFileInvoice /> Gestión de Boletas</h1>
          <p>Visualiza y gestiona todas las boletas generadas en el sistema</p>
        </header>
        {/* Controles de búsqueda y filtro */}
        <div className="invoices-controls">
          <div className="search-box">
            <FaSearch className="search-icon" />
            <input
              type="text"
              placeholder="Buscar por código o responsable..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          <div className="date-filter">
            <FaCalendarAlt className="calendar-icon" />
            <input
              type="date"
              value={filterDate}
              onChange={(e) => setFilterDate(e.target.value)}
            />
            {filterDate && (
              <button 
                className="clear-filter"
                onClick={() => setFilterDate('')}
              >
                Limpiar
              </button>
            )}
          </div>
          <button 
            className="refresh-btn"
            onClick={fetchInvoices}
            disabled={loading}
          >
            <FaSync /> {loading ? 'Actualizando...' : 'Actualizar'}
          </button>
        </div>
        {error && <div className="error-message">{error}</div>}
        {/* Tabla de boletas */}
        <div className="invoices-table-container">
          {loading ? (
            <div className="loading-message">Cargando boletas...</div>
          ) : filteredInvoices.length > 0 ? (
            <table className="invoices-table">
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Responsable</th>
                  <th>Fecha</th>
                  <th>Total</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {filteredInvoices.map((invoice) => (
                  <tr key={invoice.id}>
                    <td>{invoice.invoiceCode}</td>
                    <td>{invoice.nombreResponsable || 'N/A'}</td>
                    <td>{invoice.fechaEmision ? new Date(invoice.fechaEmision).toLocaleDateString() : 'N/A'}</td>
                    <td>${invoice.montoTotalConIVA?.toLocaleString() || '0'}</td>
                    <td>
                      <button 
                        className="download-btn"
                        onClick={() => downloadInvoice(invoice.id)}
                        title="Descargar boleta"
                        disabled={!invoice.id}
                      >
                        <FaFileDownload /> Descargar
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          ) : (
            <div className="no-results">
              {searchTerm || filterDate 
                ? 'No se encontraron boletas con los filtros aplicados' 
                : 'No hay boletas registradas en el sistema'}
            </div>
          )}
        </div>
      </main>
      <Footer />
    </div>
  );
};

export default Invoices;