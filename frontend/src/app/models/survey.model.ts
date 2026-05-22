export interface OptionResponse {
  id: number;
  text: string;
  votes: number;
}

export interface SurveyResponse {
  id: number;
  question: string;
  createdBy: string;
  active: boolean;
  createdAt: string;
  options: OptionResponse[];
}

// lo que enviamos al backend para crear/editar una encuesta
export interface SurveyRequest {
  question: string;
  options: string[]; // lista de textos: ["Sí", "No", "Tal vez"]
}

// lo que enviamos al backend para votar
export interface VoteRequest {
  optionId: number;
}