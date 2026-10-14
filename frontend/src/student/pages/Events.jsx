import React, { useState, useEffect } from 'react';
import { eventService } from '../services/eventService';
import { EventCard } from '../components/EventCard';
import { apiErrorMessage } from '../../services/api';

export const Events = () => {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function load() {
      try {
        const [local,shared]=await Promise.all([eventService.getEvents(),eventService.getSharedEvents()]);
        const sharesById = new Map();
        shared.forEach(share => {
          const destinations = sharesById.get(share.event.id) || [];
          destinations.push(share.destinationCampus);
          sharesById.set(share.event.id, destinations);
        });
        const localIds = new Set(local.map(event => event.id));
        const localEvents = local.map(event => {
          const destinations = sharesById.get(event.id) || [];
          return { ...event, sharingLabel: destinations.length ? `Shared internally for ${destinations.join(', ')}` : 'Local event' };
        });
        const sharedOnly = shared.filter(share => !localIds.has(share.event.id)).map(share => ({
          ...share.event,
          sharingLabel: `Shared internally for ${share.destinationCampus}`,
        }));
        setEvents([...localEvents, ...sharedOnly]);
      } catch (requestError) {
        setError(apiErrorMessage(requestError, 'Events could not be loaded.'));
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  return (
    <div>
      <div className="student-page-header">
        <div className="student-page-title">
          <h1>Campus Events</h1>
          <p>Discover hackathons, workshops, cultural fests, and sports tournaments</p>
        </div>
      </div>

      {loading && <div className="student-card" role="status">Loading events…</div>}
      {error && <div className="student-card" role="alert">{error}</div>}
      {!loading && !error && events.length === 0 && <div className="student-card">No campus events have been published yet.</div>}
      <div className="grid-3">
        {events.map(ev => <EventCard key={ev.id} event={ev} />)}
      </div>
    </div>
  );
};
