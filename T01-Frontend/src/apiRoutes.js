export const ROUTES = {
  KARTS: '/api/kart',
  PRICING: '/api/pricing',
  DISCOUNTS: '/api/discounts',
  BOOKINGS: '/api/bookings',
  CLIENTS: '/api/client',
  INVOICES: '/api/bookings/invoices',
  // Agrega aquí otros endpoints según los microservicios y rutas de tu gateway
};

// Devuelve la URL completa para descargar el PDF de una boleta dado su id
export const getInvoiceDownloadUrl = (id) => {
  const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';
  return `${API_BASE}/api/bookings/invoices/${id}/download`;
}; 