import api from '../../services/api';
export const electionService={
 async getElections(){return (await api.get('/elections')).data;},
 async getElectionById(id){return (await api.get(`/elections/${id}`)).data;},
 async castVote(electionId,candidateId){await api.post(`/elections/${electionId}/ballots`,{candidateId});return (await api.get(`/elections/${electionId}`)).data;},
 async getResults(id){return (await api.get(`/elections/${id}/results`)).data;}
};
