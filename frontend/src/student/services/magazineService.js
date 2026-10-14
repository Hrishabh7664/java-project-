import api from '../../services/api';
export const magazineService={async issues(){return(await api.get('/magazine/issues')).data;},async issue(id){return(await api.get(`/magazine/issues/${id}`)).data;}};
