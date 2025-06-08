import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import './home.css';
import { ROUTES } from '../apiRoutes';

const API_BASE = window.API_URL || 'http://localhost:8080';

const Home = () => {
  const [recentReservations, setRecentReservations] = useState([]);
  const [loadingReservations, setLoadingReservations] = useState(true);

  // Función para obtener todas las reservas
  const fetchReservations = async () => {
    try {
      setLoadingReservations(true);
      const response = await axios.get(`${API_BASE}${ROUTES.BOOKINGS}`);
      // Mapeamos los datos del backend al formato que necesita el frontend
      const formattedReservations = response.data.map(reservation => ({
        id: reservation.id,
        client: reservation.participantes && reservation.participantes.length > 0 ? reservation.participantes[0].nombre : '',
        kart: reservation.kartsAsignados ? reservation.kartsAsignados.join(', ') : '',
        time: reservation.fechaReserva ? new Date(reservation.fechaReserva).toLocaleString() : '',
        package: `${reservation.numVueltas} vueltas`
      }));
      setRecentReservations(formattedReservations);
    } catch (err) {
      console.error("Error al obtener reservas:", err);
    } finally {
      setLoadingReservations(false);
    }
  };

  useEffect(() => {
    fetchReservations();
  }, []);

  return (
    <div className="admin-home">
      <Navbar />
      <main className="dashboard-container">
        <header className="dashboard-header">
          <h1>Panel de Control</h1>
          <p>Bienvenido al sistema de gestión de KartingRM</p>
        </header>
        {/* Acciones rápidas */}
        <section className="quick-actions">
          <h2>Acciones Rápidas</h2>
          <div className="actions-grid">
            <Link to="/reservas" className="action-card">
              <div className="action-icon">➕</div>
              <span>Nueva Reserva</span>
            </Link>
            <Link to="/reportes" className="action-card">
              <div className="action-icon">📊</div>
              <span>Visualizar Reportes</span>
            </Link>
            <Link to="/karts" className="action-card">
              <div className="action-icon">🏎️</div>
              <span>Gestión de Karts</span>
            </Link>
            <Link to="/tarifas" className="action-card">
              <div className="action-icon">⚙️</div>
              <span>Configurar Tarifas</span>
            </Link>
          </div>
        </section>
        {/* Reservas recientes */}
        <section className="recent-section">
          <div className="section-header">
            <h2>Reservas Recientes</h2>
            <Link to="/calendar" className="view-all">Ver todas →</Link>
          </div>
          <div className="reservations-table">
            <div className="table-header">
              <span>ID Reserva</span>
              <span>Cliente</span>
              <span>Kart(s)</span>
              <span>Fecha/Hora</span>
              <span>Paquete</span>
            </div>
            {loadingReservations ? (
              <div className="loading-message">Cargando reservas...</div>
            ) : recentReservations.length > 0 ? (
              recentReservations.map((res, index) => (
                <div key={index} className="table-row">
                  <span className="reservation-id">{res.id}</span>
                  <span>{res.client}</span>
                  <span>{res.kart}</span>
                  <span>{res.time}</span>
                  <span>{res.package}</span>
                </div>
              ))
            ) : (
              <div className="no-reservations">No hay reservas registradas</div>
            )}
          </div>
        </section>
      </main>
      <Footer />
    </div>
  );
};

export default Home;