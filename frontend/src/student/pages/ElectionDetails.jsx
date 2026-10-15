import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { electionService } from '../services/electionService';
import { apiErrorMessage } from '../../services/api';

const dateOnly = (value) => String(value || '').slice(0, 10);
const todayInCollegeTimezone = () => new Intl.DateTimeFormat('en-CA', {
  timeZone: 'Asia/Kolkata', year: 'numeric', month: '2-digit', day: '2-digit'
}).format(new Date());

export const ElectionDetails = () => {
  const { id } = useParams();
  const [election, setElection] = useState(null);
  const [selected, setSelected] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const load = () => electionService.getElectionById(id)
    .then((value) => { setElection(value); setError(''); })
    .catch((e) => setError(apiErrorMessage(e, 'Election could not be loaded.')))
    .finally(() => setLoading(false));

  useEffect(() => { setLoading(true); load(); }, [id]);

  const today = todayInCollegeTimezone();
  const startDate = dateOnly(election?.startDate);
  const endDate = dateOnly(election?.endDate);
  const withinVotingDates = !!startDate && !!endDate && today >= startDate && today <= endDate;
  const canVote = election?.status === 'OPEN' && withinVotingDates && election?.eligible && !election?.userHasVoted && election?.candidates?.length > 0;

  const vote = async (event) => {
    event.preventDefault();
    if (!canVote || !selected) return;
    setBusy(true); setError(''); setMessage('');
    try {
      await electionService.castVote(id, Number(selected));
      setMessage('Your ballot has been recorded.');
      setSelected('');
      await load();
    } catch (e) {
      setError(apiErrorMessage(e, 'Vote could not be submitted.'));
    } finally { setBusy(false); }
  };

  const results = async () => {
    try { setResult(await electionService.getResults(id)); }
    catch (e) { setError(apiErrorMessage(e, 'Results are not available.')); }
  };

  let unavailableReason = '';
  if (election && !canVote) {
    if (election.userHasVoted) unavailableReason = 'Your ballot has already been recorded.';
    else if (!election.eligible) unavailableReason = 'Your student profile does not meet this election’s eligibility requirements.';
    else if (!election.candidates?.length) unavailableReason = 'There are no approved candidates available for this election.';
    else if (election.status !== 'OPEN') unavailableReason = 'Voting has not been opened by the election administrator.';
    else if (today < startDate) unavailableReason = `Voting opens on ${startDate}.`;
    else if (today > endDate) unavailableReason = `Voting ended on ${endDate}.`;
  }

  if (loading) return <div className="student-card" role="status">Loading election…</div>;
  if (!election) return <div><div className="student-card api-error-state" role="alert">{error || 'Election could not be found.'}</div><Link className="btn btn-outline" to="/student/elections">Back to elections</Link></div>;

  return <div>
    <Link className="btn btn-outline" to="/student/elections">← Elections</Link>
    <section className="student-card">
      <span className="badge badge-trust">{election.status}</span>
      <h1>{election.title}</h1><h3>{election.position}</h3>
      <p>Voting window: {startDate} – {endDate}</p>
      <p>{election.description}</p>
      <p>Eligible departments: {election.eligibleDepartments}; eligible years: {election.eligibleYears}</p>
      <p>{election.eligible ? 'Your profile meets the configured eligibility.' : 'Your profile does not meet the configured eligibility.'}</p>
      {election.userHasVoted && <p role="status">Your ballot is recorded. Candidate selections remain private.</p>}
      {unavailableReason && <p role="status" className="text-muted">{unavailableReason}</p>}
    </section>
    {error && <p role="alert" className="validation-error">{error}</p>}
    {message && <p role="status" className="success-notice">{message}</p>}
    <form onSubmit={vote}>
      <h2>Candidates</h2>
      {election.candidates.map((candidate) => <label className="student-card" key={candidate.id} style={{ display: 'block', marginBottom: '1rem' }}>
        <input type="radio" name="candidate" value={candidate.id} checked={selected === String(candidate.id)} disabled={!canVote || busy} onChange={(event) => setSelected(event.target.value)} />{' '}
        <strong>{candidate.name}</strong> · {candidate.department} {candidate.year}
        <p>{candidate.manifesto}</p>
      </label>)}
      {canVote && <button className="btn btn-primary" disabled={!selected || busy}>{busy ? 'Submitting…' : 'Confirm vote'}</button>}
    </form>
    {election.resultsPublished && <section className="student-card"><h2>Published results</h2><button className="btn btn-outline" onClick={results}>Load results</button>{result && <>{result.results.map((candidate) => <p key={candidate.id}>{candidate.name}: {candidate.votes}</p>)}<p>Ballots cast: {result.ballots}</p></>}</section>}
  </div>;
};
