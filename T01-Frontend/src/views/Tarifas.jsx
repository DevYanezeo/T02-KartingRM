import React, { useState, useEffect } from 'react';
import { FaMoneyBillWave, FaPlus, FaEdit, FaTrash, FaSearch, FaSync, FaExclamationTriangle } from 'react-icons/fa';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import './Tarifas.css';
import { ROUTES } from '../apiRoutes';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const Tarifas = () => {
  // Estados para tarifas
  const [pricings, setPricings] = useState([]);
  const [newPricing, setNewPricing] = useState({
    laps: '',
    basePrice: '',
    totalDuration: ''
  });
  const [editingPricingId, setEditingPricingId] = useState(null);
  // Estados comunes
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [successMessage, setSuccessMessage] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  // Fetch tarifas
  const fetchPricings = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(`${API_BASE}${ROUTES.PRICING}`);
      setPricings(response.data);
    } catch (error) {
      setError('Error al cargar las tarifas');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(`${API_BASE}${ROUTES.PRICING}`);
        setPricings(response.data);
      } catch (error) {
        setError('Error al cargar las tarifas');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  // Handlers para tarifas
  const handlePricingChange = (e) => {
    const { name, value } = e.target;
    setNewPricing({
      ...newPricing,
      [name]: value
    });
  };

  const handleAddPricing = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(`${API_BASE}${ROUTES.PRICING}`, newPricing);
      setPricings([...pricings, response.data]);
      setNewPricing({ laps: '', basePrice: '', totalDuration: '' });
      setSuccessMessage('Tarifa agregada correctamente');
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (error) {
      setError('Error al agregar tarifa');
    }
  };

  const handleEditPricing = (pricing) => {
    setEditingPricingId(pricing.id);
    setNewPricing({
      laps: pricing.laps,
      basePrice: pricing.basePrice,
      totalDuration: pricing.totalDuration
    });
  };

  const handleUpdatePricing = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.put(`${API_BASE}${ROUTES.PRICING}/${editingPricingId}`, newPricing);
      setPricings(pricings.map(p => p.id === editingPricingId ? response.data : p));
      setEditingPricingId(null);
      setNewPricing({ laps: '', basePrice: '', totalDuration: '' });
      setSuccessMessage('Tarifa actualizada correctamente');
      setTimeout(() => setSuccessMessage(null), 3000);
    } catch (error) {
      setError('Error al actualizar tarifa');
    }
  };

  const handleDeletePricing = async (id) => {
    if (window.confirm('¿Estás seguro de eliminar esta tarifa?')) {
      try {
        await axios.delete(`${API_BASE}${ROUTES.PRICING}/${id}`);
        setPricings(pricings.filter(p => p.id !== id));
        setSuccessMessage('Tarifa eliminada correctamente');
        setTimeout(() => setSuccessMessage(null), 3000);
      } catch (error) {
        setError('Error al eliminar tarifa');
      }
    }
  };

  // Filtrado de datos
  const filteredPricings = pricings.filter(pricing =>
    pricing.laps.toString().includes(searchTerm) ||
    pricing.basePrice.toString().includes(searchTerm) ||
    pricing.totalDuration.toString().includes(searchTerm)
  );

  return (
    <>
      <Navbar />
      <div className="tarifas-container">
        <h1 className="tarifas-title">Gestión de Tarifas</h1>
        {/* Mensajes de éxito y error */}
        {error && (
          <div className="alert alert-error">
            <FaExclamationTriangle /> {error}
            <button onClick={() => setError(null)}>×</button>
          </div>
        )}
        {successMessage && (
          <div className="alert alert-success">
            {successMessage}
            <button onClick={() => setSuccessMessage(null)}>×</button>
          </div>
        )}
        {/* Barra de búsqueda */}
        <div className="search-bar">
          <FaSearch className="search-icon" />
          <input
            type="text"
            placeholder="Buscar tarifas..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <button className="refresh-btn" onClick={fetchPricings}>
            <FaSync /> Actualizar
          </button>
        </div>
        {loading ? (
          <div className="loading">Cargando...</div>
        ) : (
          <>
            {/* Formulario de tarifas */}
            <div className="form-container">
              <h2>{editingPricingId ? 'Editar Tarifa' : 'Agregar Nueva Tarifa'}</h2>
              <form onSubmit={editingPricingId ? handleUpdatePricing : handleAddPricing}>
                <div className="form-group">
                  <label>Vueltas:</label>
                  <input
                    type="number"
                    name="laps"
                    value={newPricing.laps}
                    onChange={handlePricingChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Precio Base:</label>
                  <input
                    type="number"
                    step="0.01"
                    name="basePrice"
                    value={newPricing.basePrice}
                    onChange={handlePricingChange}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Duración Total (min):</label>
                  <input
                    type="number"
                    name="totalDuration"
                    value={newPricing.totalDuration}
                    onChange={handlePricingChange}
                    required
                  />
                </div>
                <button type="submit" className="btn-submit">
                  {editingPricingId ? 'Actualizar Tarifa' : 'Agregar Tarifa'}
                </button>
                {editingPricingId && (
                  <button
                    type="button"
                    className="btn-cancel"
                    onClick={() => {
                      setEditingPricingId(null);
                      setNewPricing({ laps: '', basePrice: '', totalDuration: '' });
                    }}
                  >
                    Cancelar
                  </button>
                )}
              </form>
            </div>
            {/* Lista de tarifas */}
            <div className="table-container">
              <h2>Listado de Tarifas</h2>
              {filteredPricings.length === 0 ? (
                <p>No hay tarifas registradas</p>
              ) : (
                <table className="data-table">
                  <thead>
                    <tr>
                      <th>Vueltas</th>
                      <th>Precio Base</th>
                      <th>Duración (min)</th>
                      <th>Acciones</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredPricings.map((pricing) => (
                      <tr key={pricing.id}>
                        <td>{pricing.laps}</td>
                        <td>${pricing.basePrice.toFixed(2)}</td>
                        <td>{pricing.totalDuration}</td>
                        <td className="actions">
                          <button
                            className="btn-edit"
                            onClick={() => handleEditPricing(pricing)}
                          >
                            <FaEdit /> Editar
                          </button>
                          <button
                            className="btn-delete"
                            onClick={() => handleDeletePricing(pricing.id)}
                          >
                            <FaTrash /> Eliminar
                          </button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          </>
        )}
      </div>
      <Footer />
    </>
  );
};

export default Tarifas;