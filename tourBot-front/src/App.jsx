import ChatBox from "./components/ChatBox";
import FileUpload from "./components/FileUpload";

function App() {
  return (
      <div className="min-h-screen bg-gray-100 flex flex-col items-center">

        {/* Header */}
        <div className="w-full max-w-4xl py-6">
          <h1 className="text-4xl font-bold text-gray-800 text-center">
            TourBot
          </h1>
        </div>

        {/* Upload */}
        <div className="w-full max-w-4xl mb-4">
          <FileUpload />
        </div>

        {/* Chat */}
        <div className="w-full max-w-4xl">
          <ChatBox />
        </div>

      </div>
    );
}

export default App;