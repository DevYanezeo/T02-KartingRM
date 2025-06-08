import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Navbar from '../components/common/Navbar';
import Footer from '../components/common/Footer';
import { FaCalendarAlt, FaChevronLeft, FaChevronRight, FaClock } from 'react-icons/fa';
import '../components/calendar/calendar.css';

const API_BASE = window.API_URL || 'http://localhost:8080';
const HOURS = Array.from({ length: 13 }, (_, i) => 10 + i); // 10 a 22
const DAYS = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];
const MINUTES_PER_SLOT = 15;
const SLOTS_PER_HOUR = 4;

function getMonday(d) {
  const date = new Date(d);
  const day = date.getDay();
  const diff = date.getDate() - day + (day === 0 ? -6 : 1); // Ajuste para que la semana empiece en lunes
  return new Date(date.setDate(diff));
}

function formatDate(date) {
  return date.toISOString().split('T')[0];
}

function formatDisplayDate(date) {
  const options = { day: 'numeric', month: 'long' };
  return new Date(date).toLocaleDateString('es-ES', options);
}

function parseTime(timeStr) {
  const [hours, minutes] = timeStr.split(':').map(Number);
  return hours * 60 + minutes;
}

function calculatePosition(startTime) {
  const totalMinutes = parseTime(startTime);
  const startOfDay = 10 * 60; // 10:00 AM en minutos
  const minutesFromStart = totalMinutes - startOfDay;
  return minutesFromStart;
}

function calculateDuration(startTime, endTime) {
  const start = parseTime(startTime);
  const end = parseTime(endTime);
  return end - start;
}

// Función para generar un color basado en el código de reserva
function generateBookingColor(bookingCode) {
  let hash = 0;
  for (let i = 0; i < bookingCode.length; i++) {
    hash = bookingCode.charCodeAt(i) + ((hash << 5) - hash);
  }
  const hue = hash % 360;
  return `hsl(${hue}, 70%, 35%)`; // Saturación y luminosidad fijas para asegurar contraste
}

const CalendarView = () => {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [weekDates, setWeekDates] = useState([]);
  const [rackData, setRackData] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const monday = getMonday(currentDate);
    const dates = Array.from({ length: 7 }, (_, i) => {
      const date = new Date(monday);
      date.setDate(monday.getDate() + i);
      return formatDate(date);
    });
    setWeekDates(dates);
    fetchRackData(dates);
  }, [currentDate]);

  const fetchRackData = async (dates) => {
    setLoading(true);
    try {
      const promises = dates.map(date =>
        axios.get(`${API_BASE}/api/rack/by-date?date=${date}`)
      );
      const responses = await Promise.all(promises);
      const newData = {};
      dates.forEach((date, index) => {
        newData[date] = responses[index].data;
      });
      setRackData(newData);
    } catch (error) {
      console.error('Error fetching rack data:', error);
    } finally {
      setLoading(false);
    }
  };

  const navigateWeek = (direction) => {
    const newDate = new Date(currentDate);
    newDate.setDate(currentDate.getDate() + (direction * 7));
    setCurrentDate(newDate);
  };

  // Renderizar slots de 15 minutos
  const renderTimeSlots = (hour) => {
    return Array.from({ length: SLOTS_PER_HOUR }, (_, index) => {
      const minutes = index * MINUTES_PER_SLOT;
      return (
        <div key={`${hour}-${minutes}`} className="quarter-hour-slot">
          {minutes === 0 && (
            <div className="hour-label">
              <FaClock className="clock-icon" />
              {hour}:00
            </div>
          )}
        </div>
      );
    });
  };

  // Renderizar bloques de reserva
  const renderBlocks = (date, hour) => {
    const blocks = rackData[date] || [];
    const hourStart = `${hour}:00`;
    const hourEnd = `${hour + 1}:00`;
    
    // Filtrar bloques que intersectan con esta hora
    const relevantBlocks = blocks.filter(block => {
      const blockStart = parseTime(block.horaInicio);
      const blockEnd = parseTime(block.horaFin);
      const slotStart = parseTime(hourStart);
      const slotEnd = parseTime(hourEnd);
      return blockStart < slotEnd && blockEnd > slotStart;
    });

    return relevantBlocks.map(block => {
      const startMinutes = calculatePosition(block.horaInicio);
      const duration = calculateDuration(block.horaInicio, block.horaFin);
      const backgroundColor = generateBookingColor(block.bookingCode);
      const style = {
        top: `${(startMinutes % 60) * (100 / 60)}%`,
        height: `${duration * (100 / 60)}%`,
        position: 'absolute',
        width: '95%',
        left: '2.5%',
        backgroundColor
      };

      const kartsText = block.kartsAsignados ? block.kartsAsignados.join(' ') : '';

      return (
        <div
          key={block.bookingCode}
          className={`rack-block occupied duration-${duration}`}
          style={style}
          title={`${block.bookingCode}\n${block.horaInicio} - ${block.horaFin}\n${kartsText}`}
        >
          <div className="booking-info">
            <div className="booking-code">{block.bookingCode}</div>
            <div className="booking-time">{block.horaInicio} - {block.horaFin}</div>
            {block.kartsAsignados && block.kartsAsignados.length > 0 && (
              <div className="karts-text">{kartsText}</div>
            )}
          </div>
        </div>
      );
    });
  };

  return (
    <div className="page-container">
      <Navbar />
      <div className="content-wrapper">
        <div className="calendar-container">
          <div className="calendar-header">
            <div className="calendar-title">
              <FaCalendarAlt />
              Calendario de Reservas
            </div>
            <div className="calendar-navigation">
              <button className="nav-button" onClick={() => navigateWeek(-1)}>
                <FaChevronLeft /> Semana Anterior
              </button>
              <button className="nav-button" onClick={() => navigateWeek(1)}>
                Siguiente Semana <FaChevronRight />
              </button>
            </div>
          </div>

          <div className="calendar-grid-container">
            <div className="calendar-grid">
              <div className="time-column">
                <div className="time-header">Hora</div>
                {HOURS.map(hour => (
                  <div key={hour} className="hour-cell">
                    {renderTimeSlots(hour)}
                  </div>
                ))}
              </div>

              <div className="days-grid">
                <div className="days-header">
                  {DAYS.map((day, index) => (
                    <div key={day} className="day-column-header">
                      <div className="day-name">{day}</div>
                      <div className="day-date">{formatDisplayDate(weekDates[index])}</div>
                    </div>
                  ))}
                </div>

                <div className="time-slots">
                  {HOURS.map(hour => (
                    <div key={hour} className="hour-row">
                      {weekDates.map(date => (
                        <div key={`${date}-${hour}`} className="time-slot">
                          <div className="quarter-hour-grid">
                            {renderTimeSlots(hour)}
                          </div>
                          {renderBlocks(date, hour)}
                        </div>
                      ))}
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
          {loading && (
            <div className="loading-overlay">
              <div className="loading-spinner"></div>
              <div className="loading-text">Cargando datos del rack...</div>
            </div>
          )}
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default CalendarView;
