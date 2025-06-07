import React, { useState } from 'react';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import { FaUserPlus, FaTrash, FaCalendarAlt, FaClock, FaCheck } from 'react-icons/fa';
import './Reservas.css';
import { ROUTES } from '../apiRoutes';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

const Reservas = () => {
  const [fechaUso, setFechaUso] = useState('');
  const [horaUso, setHoraUso] = useState('');
  const [numVueltas, setNumVueltas] = useState(10);
  const [participantes, setParticipantes] = useState([
    { nombre: '', email: '', fechaNacimiento: '' }
  ]);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleParticipanteChange = (index, e) => {
    const { name, value } = e.target;
    setParticipantes(prev => prev.map((p, i) => i === index ? { ...p, [name]: value } : p));
  };

  const addParticipante = () => {
    setParticipantes(prev => [...prev, { nombre: '', email: '', fechaNacimiento: '' }]);
  };

  const removeParticipante = (index) => {
    setParticipantes(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setLoading(true);
    // Validación básica
    if (!fechaUso || !horaUso) {
      setError('Debes ingresar la fecha y hora de uso.');
      setLoading(false);
      return;
    }
    if (participantes.length === 0 || participantes.some(p => !p.nombre || !p.email || !p.fechaNacimiento)) {
      setError('Todos los participantes deben tener nombre, email y fecha de nacimiento.');
      setLoading(false);
      return;
    }
    const fechaUsoISO = `${fechaUso}T${horaUso}:00`;
    const reserva = {
      fechaUso: fechaUsoISO,
      numVueltas: Number(numVueltas),
      participantes
    };
    try {
      await axios.post(`${API_BASE}${ROUTES.BOOKINGS}`, reserva);
      setSuccess('¡Reserva creada exitosamente!');
      setFechaUso('');
      setHoraUso('');
      setNumVueltas(10);
      setParticipantes([{ nombre: '', email: '', fechaNacimiento: '' }]);
    } catch (err) {
      setError('Error al crear la reserva');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="reservas-page">
      <Navbar />
      <main className="reservas-container">
        <h1>Crear Reserva</h1>
        <form className="reserva-form" onSubmit={handleSubmit}>
          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}
          <div className="form-row">
            <div className="form-group">
              <label><FaCalendarAlt /> Fecha de uso</label>
              <input type="date" value={fechaUso} onChange={e => setFechaUso(e.target.value)} required />
            </div>
            <div className="form-group">
              <label><FaClock /> Hora de uso</label>
              <input type="time" value={horaUso} onChange={e => setHoraUso(e.target.value)} required />
            </div>
            <div className="form-group">
              <label>Vueltas</label>
              <select value={numVueltas} onChange={e => setNumVueltas(e.target.value)}>
                <option value={10}>10</option>
                <option value={15}>15</option>
                <option value={20}>20</option>
              </select>
            </div>
          </div>
          <h2>Participantes</h2>
          {participantes.map((p, idx) => (
            <div className="participante-row" key={idx}>
              <div className="form-group">
                <label>Nombre</label>
                <input type="text" name="nombre" value={p.nombre} onChange={e => handleParticipanteChange(idx, e)} required />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input type="email" name="email" value={p.email} onChange={e => handleParticipanteChange(idx, e)} required />
              </div>
              <div className="form-group">
                <label>Fecha de nacimiento</label>
                <input type="date" name="fechaNacimiento" value={p.fechaNacimiento} onChange={e => handleParticipanteChange(idx, e)} required />
              </div>
              {participantes.length > 1 && (
                <button type="button" className="btn-remove" onClick={() => removeParticipante(idx)}><FaTrash /></button>
              )}
            </div>
          ))}
          <button type="button" className="btn-secondary" onClick={addParticipante}><FaUserPlus /> Agregar Participante</button>
          <button type="submit" className="btn-primary" disabled={loading}><FaCheck /> Crear Reserva</button>
        </form>
      </main>
      <Footer />
    </div>
  );
};

export default Reservas;