export const ROUTES = {
  KARTS: '/api/kart',
  PRICING: '/api/pricing',
  DISCOUNTS: '/api/discounts',
  BOOKINGS: '/api/bookings',
  CLIENTS: '/api/client',
  INVOICES: '/api/bookings/invoices',
  INCOME_BY_LAPS: '/api/reports/ingresos-por-vueltas',
  INCOME_BY_PEOPLE: '/api/reports/ingresos-por-personas',
  // Agrega aquí otros endpoints según los microservicios y rutas de tu gateway
};

// Devuelve la URL completa para descargar el PDF de una boleta dado su id
export const getInvoiceDownloadUrl = (id) => {
  const API_BASE = window.API_URL || 'http://localhost:8080';
  return `${API_BASE}/api/bookings/invoices/${id}/download`;
};