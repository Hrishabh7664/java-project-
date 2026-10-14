import api from '../../services/api';
export const electionManagementService={
 async getElections(){return (await api.get('/elections')).data;},
 async createElection(data){return (await api.post('/elections',data)).data;},
 async updateElection(id,data){return (await api.put(`/elections/${id}`,data)).data;},
 async addCandidate(id,data){return (await api.post(`/elections/${id}/candidates`,data)).data;},
 async updateCandidateStatus(electionId,candidateId,approved){return (await api.put(`/elections/${electionId}/candidates/${candidateId}`,{approved})).data;},
 async updateLifecycle(id,data){return (await api.put(`/elections/${id}/lifecycle`,data)).data;},
 async getHistory(id){return (await api.get(`/elections/${id}/history`)).data;},
 async getResults(id){return (await api.get(`/elections/${id}/results`)).data;}
};
